(ns clojure-crawl.gui
  (:import (javax.swing JFrame JMenuBar JMenu UIManager
			JMenuItem JOptionPane JPanel JLabel
			JList JTextField JTextArea JButton
			JScrollPane ListSelectionModel
			BorderFactory JTabbedPane)
	   (javax.swing.border TitledBorder)
	   (java.awt Dimension Color BorderLayout)
	   (java.awt.event MouseListener KeyEvent))
  (:use clojure-crawl.actors
	clojure-crawl.races
	clojure-crawl.classes
	clojure-crawl.utils
	clojure-crawl.mapcanvas
	clojure-crawl.guiutils)
  (:require [clojure-crawl [game :as game]]
	    [clojure-crawl [levels :as levels]]
	    [clojure-crawl [items :as items]]
	    [clojure-crawl [keylistener :as keylistener]]
	    [clojure-crawl [regen :as regen]]))

;; set default look and feel
(UIManager/setLookAndFeel (UIManager/getSystemLookAndFeelClassName))

(defrecord Gui-player [name race clazz strength agility health magic exp life mana level attack defense critical evade life-regen mana-regen hide act-skills all-skills equip bag])

(defrecord Gui-enemy [name strength agility health magic life mana level attack defense critical evade aware skills])

(defrecord Gui-treasure [description])

;; variables
(def ^JFrame gameframe (new JFrame "Clojure Crawler"))

(def ^JTabbedPane gamepanel (new JTabbedPane))

(def ^JPanel newpanel (new JPanel))

(def ^JPanel mapcanvas (create-map-canvas))

(def ^JLabel statuslabel (new JLabel "" JLabel/RIGHT))

(def ^JPanel statusbar (new JPanel))

(def gui-player (new Gui-player (new JLabel) (new JLabel) (new JLabel)
		     (new JLabel) (new JLabel) (new JLabel)
		     (new JLabel) (new JLabel) (new JLabel) (new JLabel) (new JLabel)
		     (new JLabel) (new JLabel) (new JLabel) (new JLabel)
		     (new JLabel) (new JLabel) (new JLabel)
		     (new JList) [(new JList) (new JTextArea)]
		     [(new JList) (new JTextArea)] [(new JList) (new JTextArea)]))

(def gui-enemy (new Gui-enemy (new JLabel) (new JLabel) (new JLabel) (new JLabel)
		    (new JLabel) (new JLabel) (new JLabel) (new JLabel) (new JLabel)
		    (new JLabel) (new JLabel) (new JLabel) (new JLabel) (new JLabel)))

(def gui-treasure (new Gui-treasure (new JTextArea)))

;; helpers
(defn- repaint-map []
  (. mapcanvas repaint))

(defn- status-print [s]
  (. statuslabel setText s))

(defn- gui-go [side]
  (if (game/can-go? side)
    (do
      (game/go side)
      (let [enemy (game/current-enemy)]
	(when (and enemy (not @(:aware enemy)))
	  (status-print "You are hidden!")))
      (repaint-map))
    (status-print (str "Cannot go " (key->name side)))))

;; functions
(defn- set-gui-player []
  (when-let [player @(:player game/game)]
    (let [skills @(:skills player)
	  [list _] (:all-skills gui-player)
	  act-list (:act-skills gui-player)]
      (set-texts [(:name gui-player) (:name player)
		  (:race gui-player) (:name (:race player))
		  (:clazz gui-player) (:name @(:clazz player))
		  (:strength gui-player) (str (strength player))
		  (:agility gui-player) (str (agility player))
		  (:health gui-player) (str (health player))
		  (:magic gui-player) (str (magic player))
		  (:exp gui-player) (str @(:exp player) "/" (levels/next-level-xp player))
		  (:life gui-player) (str (pos-num @(:life player)) "/" (pos-num (max-life player)))
		  (:mana gui-player) (str (pos-num @(:mana player)) "/" (pos-num (max-mana player)))
		  (:level gui-player) (str @(:level player))
		  (:attack gui-player) (vec->damage (base-attack player))
		  (:defense gui-player) (str (to-num (defense player)))
		  (:critical gui-player) (str (to-num (critical player)) "%")
		  (:evade gui-player) (str (to-num (evade player)) "%")
		  (:life-regen gui-player) (str (to-num (life-regen player)) " per second")
		  (:mana-regen gui-player) (str (to-num (mana-regen player)) " per second")
		  (:hide gui-player) (str (to-num (hide player)) "%")])
      (. list setListData (object-array (map :name skills)))
      (. act-list setListData (object-array (map :name (filter :active? skills)))))))

(defn- reset-gui-enemy []
  (doseq [ks (keys gui-enemy)]
    (. (ks gui-enemy) setText "")))

(defn- set-gui-enemy []
  (when-let [enemy (:enemy (game/current-room))]
    (if (dead? enemy)
      (reset-gui-enemy)
      (do
	(set-texts [(:name gui-enemy) (:name enemy)
		    (:strength gui-enemy) (str (strength enemy))
		    (:agility gui-enemy) (str (agility enemy))
		    (:health gui-enemy) (str (health enemy))
		    (:magic gui-enemy) (str (magic enemy))
		    (:life gui-enemy) (str (pos-num @(:life enemy)) "/" (max-life enemy))
		    (:mana gui-enemy) (str (pos-num @(:mana enemy)) "/" (max-mana enemy))
		    (:level gui-enemy) (str (:level enemy))
		    (:attack gui-enemy) (vec->damage (base-attack enemy))
		    (:defense gui-enemy) (str (to-num (defense enemy)))
		    (:critical gui-enemy) (str (to-num (critical enemy)) "%")
		    (:evade gui-enemy) (str (to-num (evade enemy)) "%")
		    (:aware gui-enemy) (str @(:aware enemy))
		    (:skills gui-enemy) (str (vec (map :name (:skills enemy))))])
	(. gameframe repaint)))))

(defn- reset-gui-treasure []
  (. (:description gui-treasure) setText ""))

(defn- set-gui-treasure []
  (let [tre (:treasure (game/current-room))]
    (if (or (not tre) (not @tre))
      (reset-gui-treasure)
      (do
	(. (:description gui-treasure) setText (items/show-item @tre))
	(. gameframe repaint)))))

(defn- reset-gui-bag []
  (. (second (:bag gui-player)) setText ""))

(defn- set-gui-bag []
  (when-let [player @(:player game/game)]
    (let [bag @(:bag player)
	  gbag (first (:bag gui-player))]
      (. gbag setListData (object-array (map items/item-name bag)))
      (. gameframe repaint))))

(defn- reset-game []
  (game/reset)
  (reset-gui-enemy))

(defn init-gui []
  (reset-game)
  (. gameframe setContentPane newpanel)
  (. gameframe setMinimumSize (new Dimension 800 800))
  (. gameframe setResizable false)
  (. gameframe setVisible true))

(defn- game-over []
  (show-message gameframe "Game Over" "You died!")
  (init-gui))

(defn- gui-enemy-turn []
  (let [action (ai-action (game/current-enemy) (game/player))]
    (cond (:attack action)
	  (game/attack-player)
	  (:skill action)
	  (game/use-skill-enemy (:skill action)))))

(defn- gui-player-action [act-fun fail-msg]
  (let [enemy (game/current-enemy)]
    (if (and enemy (not (dead? enemy)))
      (let [res (act-fun)
	    mergef (fn [[k v]]
		     (str (key->name k) " +" (to-num v) "\n"))]
	(if res
	  (if (dead? enemy)
	    (do
	      (status-print (str (attack->str res) " Enemy is dead"))
	      (regen/on))
	    (let [res2 (gui-enemy-turn)]
	      (status-print (str "Player: " (attack->str res)
				 " Enemy: " (attack->str res2)))
	      (when (dead? (game/player))
		(game-over))))
	  (status-print fail-msg))
	(let [ldata (:level-data res)]
	  (when (and res ldata)
	    (show-message gameframe
			  (str "Strength +" (to-num (:strength ldata)) "\n"
			       "Agility +" (to-num (:agility ldata)) "\n"
			       "Health +" (to-num (:health ldata)) "\n"
			       "Magic +" (to-num (:magic ldata)) "\n"
			       "Life +" (to-num (:life ldata)) "\n"
			       "Mana +" (to-num (:mana ldata)) "\n"
			       "Attack [+" (to-num (first (:attack ldata)))
			       " +" (to-num (second (:attack ldata))) "]\n"
			       "Critical +" (to-num (:critical ldata)) "\n"
			       "Evade +" (to-num (:evade ldata)) "\n"
			       "Life Regen +" (to-num (:life-regen ldata)) "\n"
			       "Mana Regen +" (to-num (:mana-regen ldata)))
			  "Level Up!")))
	(let [ldata (:skill-level-data res)]
	  (when (and res ldata)
	    (show-message gameframe
			  (str "Attack +" (to-num (:attack ldata)) "\n"
			       "Critical +" (to-num (:critical ldata)) "\n"
			       "Consume +" (to-num (:consume ldata)))
			  (str (:name (:skill res)) " Level Up!"))))
	(set-gui-player)
	(set-gui-enemy)
	(set-gui-treasure)
	(repaint-map))
      (status-print "No enemy in the room"))))

(defn- gui-attack []
  (gui-player-action game/attack-enemy "No enemy to attack"))

(defn- gui-skill [name]
  (gui-player-action #(game/use-skill-player name) "Not enough mana"))

(defn- create-all-skills-panel []
  (let [^JPanel panel (new JPanel)
        border (BorderFactory/createTitledBorder "Skills")
        [jlist tarea] (:all-skills gui-player)
        list (new JScrollPane jlist)
        area (new JScrollPane tarea)]
    (set-bounds [list [10 20 90 210]
                 area [110 20 200 210]])
    (. tarea setEditable false)
    (. jlist addListSelectionListener
      (list-selection-listener (when-let [name (. jlist getSelectedValue)]
                                 (. tarea setText (game/show-skill name @(:player game/game))))))
    (doto panel
      (. setSize (new Dimension 320 245))
      (. setBorder border)
      (. setLayout nil)
      (. add list)
      (. add area))))

(defn- create-playerpanel []
  (let [^JPanel panel (new JPanel)
        labels ["Name:" "Race:" "Class:" "Strength:" "Agility:" "Health:" "Magic:"
                "Experience:" "Life:" "Mana:" "Level:" "Attack:" "Defense:" "Critical:"
                "Evade:" "Life regen:" "Mana regen:" "Hide:"]
        y (atom 15)
        values (vals gui-player)
        sequ (partition 2 (interleave labels values))
        border (BorderFactory/createTitledBorder "Player")]
    (doto panel
      (. setSize (new Dimension 250 360))
      (. setBorder border)
      (. setLayout nil))
    (doseq [[^String l ^JPanel v] sequ]
      (let [^JLabel label (new JLabel l)]
        (. label setBounds 10 @y 80 18)
        (. v setBounds 110 @y 120 18)
        (. panel add label)
        (. panel add v)
        (swap! y + 19)))
    panel))

(defn- create-enemypanel []
  (let [^JPanel panel (new JPanel)
        labels ["Name:" "Strength:" "Agility:" "Health:" "Magic:"
                "Life:" "Mana:" "Level:" "Attack:" "Defense:" "Critical:"
                "Evade:" "Aware:" "Skills:"]
        y (atom 15)
        values (vals gui-enemy)
        sequ (partition 2 (interleave labels values))
        border (BorderFactory/createTitledBorder "Enemy")]
    (doto panel
      (. setSize (new Dimension 250 320))
      (. setBorder border)
      (. setLayout nil))
    (doseq [[^String l ^JPanel v] sequ]
      (let [^JLabel label (new JLabel l)]
        (. label setBounds 10 @y 80 18)
        (. v setBounds 110 @y 120 18)
        (. panel add label)
        (. panel add v)
        (swap! y + 19)))
    panel))

(defn- create-actionpanel []
  (let [^JPanel panel (new JPanel)
	^JButton usesk (new JButton "use skill")
	^JButton att (new JButton "attack")
        border (BorderFactory/createTitledBorder "Actions")
	list (:act-skills gui-player)
	scpane (new JScrollPane list)]
    (add-action-listener att (gui-attack))
    (add-action-listener usesk
			 (if-let [name (. list getSelectedValue)]
			   (gui-skill name)
			   (status-print "No skill selected")))
    (set-bounds [scpane [10 20 140 190]
		 att [160 20 80 25]
		 usesk [160 50 80 25]])
    (add-all panel usesk att scpane)
    (doto panel
      (. setLayout nil)
      (. setSize 250 220)
      (. setBorder border))
    panel))

(defn- create-treasurepanel []
  (let [panel (new JPanel)
	pick (new JButton "pick up")
	border (BorderFactory/createTitledBorder "Treasure / Drop")
	area (:description gui-treasure)
	scpane (new JScrollPane area)]
    (set-bounds [scpane [10 20 230 160]
		 pick [10 185 80 25]])
    (. area setEditable false)
    (add-all panel pick scpane)
    (add-action-listener pick
			 (let [res (game/pickup-item)]
			   (cond (= res :added)
				 (status-print "item added to bag.")
				 (= res :full)
				 (status-print "bag is full.")
				 (= res :no-item)
				 (status-print "no item to pick up."))
			   (repaint-map)
			   (reset-gui-treasure)
			   (set-gui-bag)))
    (doto panel
      (.setLayout nil)
      (.setSize 250 220)
      (.setBorder border))
    panel))

(defn- create-bagpanel []
  (let [panel (new JPanel)
	equip (new JButton "equip")
	use (new JButton "use")
	delete (new JButton "delete")
        border (BorderFactory/createTitledBorder "Bag")
	[list area] (:bag gui-player)
	scpanedesc (new JScrollPane area)
	scpanelist (new JScrollPane list)]
    (add-action-listener equip
			 (when-let [i (. list getSelectedIndex)]
			   (let [item (nth @(:bag (game/player)) i)
				 res (game/equip-item item)]
			     (if res
			       (do
				 (game/remove-item item)
				 (reset-gui-bag)
				 (set-gui-bag)
				 (set-gui-player)
				 (status-print "Item equipped."))
			       (status-print "Failed. You have to unequip first.")))))
    (add-action-listener delete
			 (when-let [i (. list getSelectedIndex)]
			   (let [item (nth @(:bag (game/player)) i)]
			     (game/remove-item item)
			     (reset-gui-bag)
			     (set-gui-bag))))
    (add-action-listener use
			 (when-let [i (. list getSelectedIndex)]
			   (let [item (nth @(:bag (game/player)) i)]
			     (if (= (:type item) :item)
			       (do
				 (game/use-item item)
				 (game/remove-item item)
				 (reset-gui-bag)
				 (set-gui-bag)
				 (set-gui-player)
				 (status-print "Item successfully used"))
			       (status-print "Cannot use equipment")))))
    (set-bounds [scpanelist [10 20 140 220]
		 scpanedesc [160 20 240 220]
		 equip [10 250 80 25]
		 use [100 250 80 25]
		 delete [190 250 80 25]])
    (. area setEditable false)
    (. list addListSelectionListener
       (list-selection-listener (let [i (. list getSelectedIndex)
				      bag @(:bag (game/player))]
				  (when (and i (>= i 0) (> (count bag) i))
				    (let [item (nth bag i)]
				      (. area setText (items/show-item item)))))))
    (add-all panel equip use delete scpanelist scpanedesc)
    (doto panel
      (. setLayout nil)
      (. setSize 420 300)
      (. setBorder border))
    panel))

(defn- create-equippanel []
  (let [panel (new JPanel)
	unequip (new JButton "unequip")
        border (BorderFactory/createTitledBorder "Equipment")
	[list area] (:equip gui-player)
	scpanedesc (new JScrollPane area)
	scpanelist (new JScrollPane list)]
    (add-action-listener unequip
			 (let [k (. list getSelectedValue)
			       equip @(:equip (game/player))]
			   (when (and k (not (. k isEmpty)))
			     (when-let [item ((name->key k) equip)]
			       (game/unequip-item item)
			       (game/pickup-item item)
			       (set-gui-bag)
			       (set-gui-player)
			       (. area setText "")))))
    (set-bounds [scpanelist [10 20 90 220]
		 scpanedesc [110 20 290 220]
		 unequip [10 250 80 25]])
    (. area setEditable false)
    (. list setListData (object-array ["Weapon" "Armor" "Shield" "Ring"]))
    (. list addListSelectionListener
       (list-selection-listener (let [k (. list getSelectedValue)
				      equip @(:equip (game/player))]
				  (when (and k (not (. k isEmpty)))
				    (if-let [item ((name->key k) equip)]
				      (. area setText (items/show-item item))
				      (. area setText ""))))))
    (add-all panel unequip scpanelist scpanedesc)
    (doto panel
      (. setLayout nil)
      (. setSize 420 300)
      (. setBorder border))
    panel))

(defn- create-statusbar []
  (doto statusbar
    (. setLayout nil)
    (. setSize 800 25)
    (. add statuslabel))
  (set-bounds [statuslabel [0 0 790 25]]))

(defn- gui-move [where]
  (status-print "")
  (gui-go where)
  (if (game/has-enemy?)
    (regen/off)
    (regen/on))
  (set-gui-enemy)
  (set-gui-treasure))

(defn- create-gamepanel []
  (let [playerpanel (create-playerpanel)
        enemypanel (create-enemypanel)
        skills (create-all-skills-panel)
        playertab (new JPanel)
        gametab (new JPanel)
	actionpanel (create-actionpanel)
	treasurepanel (create-treasurepanel)
	bagpanel (create-bagpanel)
	equippanel (create-equippanel)]
    (set-gui-player)
    (. skills setLocation 0 380)
    (. mapcanvas setLocation 280 10)
    (. enemypanel setLocation 0 380)
    (. actionpanel setLocation 270 480)
    (. treasurepanel setLocation 530 480)
    (. bagpanel setLocation 350 380)
    (. equippanel setLocation 350 30)
    (. gamepanel addChangeListener
      (change-listener (let [pane (. e getSource)
                             name (. pane getTitleAt (. pane getSelectedIndex))]
                         (if (= name "Game")
                           (. gametab add playerpanel)
                           (. playertab add playerpanel)))))
    (. mapcanvas addKeyListener (keylistener/key-listener))
    (keylistener/add-key-released KeyEvent/VK_LEFT
				  (fn [] (gui-move :left)))
    (keylistener/add-key-released KeyEvent/VK_RIGHT
				  (fn [] (gui-move :right)))
    (keylistener/add-key-released KeyEvent/VK_UP
				  (fn [] (gui-move :front)))
    (keylistener/add-key-released KeyEvent/VK_DOWN
				  (fn [] (gui-move :rear)))
    (keylistener/add-key-released KeyEvent/VK_HOME
				  (fn [] (do
					    (game/descend)
					    (status-print "")
					    (repaint-map))))
    (keylistener/add-key-released KeyEvent/VK_END
				  (fn [] (do
					    (game/ascend)
					    (status-print "")
					    (repaint-map))))
    (keylistener/add-key-released KeyEvent/VK_P
				  (fn [] (let [res (game/pickup-item)]
					   (cond (= res :added)
						 (status-print "item added to bag.")
						 (= res :full)
						 (status-print "bag is full.")
						 (= res :no-item)
						 (status-print "no item to pick up."))
					   (repaint-map)
					   (reset-gui-treasure)
					   (set-gui-bag))))
    (keylistener/add-key-released KeyEvent/VK_S
				  (fn [] (if-let [name (. (:act-skills gui-player) getSelectedValue)]
					   (gui-skill name)
					   (status-print "No skill selected"))))
    (keylistener/add-key-released KeyEvent/VK_A
				  (fn [] (gui-attack)))
    (.addMouseListener mapcanvas
		       (proxy [MouseListener] []
			 (mousePressed [e]
				       (.requestFocusInWindow mapcanvas))
			 (mouseEntered [e])
			 (mouseExited [e])
			 (mouseClicked [e])
			 (mouseReleased [e])))
    (add-all gametab mapcanvas enemypanel
	     actionpanel treasurepanel)
    (add-all playertab bagpanel equippanel skills)
    (doto gamepanel
      (. addTab "Game" gametab)
      (. addTab "Player" playertab)
      (. setSize 800 730))
    (doto gametab
      (. setLayout nil)
      (. setSize 800 710))
    (doto playertab
      (. setLayout nil)
      (. setSize 800 710))))

(defn- how-to-play []
  (show-message gameframe (str "right - go right\n"
			       "left - go left\n"
			       "up - go up\n"
			       "down - go down\n"
			       "home - descend a level\n"
			       "end - ascend a level\n"
			       "a - attack\n"
			       "s - use selected skill\n"
			       "p - pickup drop/treasure\n\n"
			       "*IMPORTANT: if the keyboard stop "
			       "responding click on the map") "How to play"))

(defn- goto-game [pname race clazz]
  (let [^JPanel panel (new JPanel)]
    (game/start-game pname race clazz)
    (set-gui-player)
    (set-gui-treasure)
    (set-bounds [panel [0 20 800 780]])
    (. gamepanel setLocation 0 0)
    (. statusbar setLocation 0 730)
    (doto panel
      (. setLayout nil)
      (. add gamepanel)
      (. add statusbar))
    (. gameframe setContentPane panel)
    (how-to-play)
    (.requestFocusInWindow mapcanvas)
    (regen/set-repaint set-gui-player)
    (regen/on)
    (regen/start)))

(defn- init-menu []
  (let [mainmenu (new JMenuBar)
        gamemenu (new JMenu "game")
        saveitem (new JMenuItem "save")
        loaditem (new JMenuItem "load")
	howitem (new JMenuItem "how to play")
        exititem (new JMenuItem "exit")
        helpmenu (new JMenu "help")
        aboutitem (new JMenuItem "about")]
    (. gamemenu add saveitem)
    (. gamemenu add loaditem)
    (. gamemenu add howitem)
    (. gamemenu add exititem)
    (. helpmenu add aboutitem)
    (add-action-listener saveitem
			 (game/save))
    (add-action-listener aboutitem
			 (show-message gameframe "Clojure Crawler - a dungeon crawler made in clojure.
Source released under Eclipse License.
http://github.com/stackoverflow/clojure-crawl"
				       "Clojure Crawler"))
    (add-action-listener howitem
			 (how-to-play))
    (. mainmenu add gamemenu)
    (. mainmenu add helpmenu)
    (. gameframe setJMenuBar mainmenu)))

(defn- create-newpanel []
  (let [racelist (new JList (object-array (race-names)))
        classlist (new JList (object-array (class-names)))
        namefield (new JTextField)
        racedesc (new JTextArea)
        classdesc (new JTextArea)
        newbutton (new JButton "new game")
        racelabel (new JLabel "Race")
        racedesclabel (new JLabel "Race description")
        classlabel (new JLabel "Class")
        classdesclabel (new JLabel "Class description")
        namelabel (new JLabel "Name")
        racelistsc (new JScrollPane racelist)
        racedescsc (new JScrollPane racedesc)
        classlistsc (new JScrollPane classlist)
        classdescsc (new JScrollPane classdesc)]
    (. racedesc setEditable false)
    (. classdesc setEditable false)
    (. racelist setSelectionMode ListSelectionModel/SINGLE_SELECTION)
    (. classlist setSelectionMode ListSelectionModel/SINGLE_SELECTION)
    ;; positions
    (set-bounds [namelabel [20 10 100 20]
                 namefield [20 30 250 25]
                 racelabel [20 60 100 20]
                 racelistsc [20 80 200 200]
                 classlabel [20 290 100 20]
                 classlistsc [20 310 200 200]
                 newbutton [20 530 100 25]
                 racedesclabel [350 10 100 20]
                 racedescsc [350 30 350 280]
                 classdesclabel [350 320 100 20]
                 classdescsc [350 340 350 280]])
    ;; listeners
    (. racelist addListSelectionListener
      (list-selection-listener (let [name (. racelist getSelectedValue)]
                                 (. racedesc setText (show-race name)))))
    (. classlist addListSelectionListener
      (list-selection-listener (let [name (. classlist getSelectedValue)]
                                 (. classdesc setText (show-class name)))))
    (add-action-listener newbutton
			 (let [^String name (. namefield getText)
			       ^String race (. racelist getSelectedValue)
			       ^String clazz (. classlist getSelectedValue)]
			   (if (. name isEmpty)
			     (show-message gameframe "Please insert a name" "Error")
			     (if (or (not race)
				     (. race isEmpty))
			       (show-message gameframe "Please choose a race" "Error")
			       (if (or (not clazz)
				       (. clazz isEmpty))
				 (show-message gameframe "Please choose a class" "Error")
				 (goto-game name race clazz))))))
    (. newpanel setLayout nil)
    ;; add
    (add-all newpanel racelabel racedesclabel classlabel classdesclabel
	     namelabel newbutton racelistsc racedescsc
	     classlistsc namefield classdescsc)))

(defn create-gui []
  (init-menu)
  (create-newpanel)
  (create-statusbar)
  (create-gamepanel))

(defn create-and-init []
  (create-gui)
  (init-gui))

(defn show-gui []
  (. gameframe setVisible true)
  (repaint-map))