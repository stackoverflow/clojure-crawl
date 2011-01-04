(ns clojure-crawl.gui
  (:import (javax.swing JFrame JMenuBar JMenu UIManager
			JMenuItem JOptionPane JPanel JLabel
			JList JTextField JTextArea JButton
			JScrollPane ListSelectionModel
			BorderFactory JTabbedPane JComponent)
	   (javax.swing.border TitledBorder)
	   (java.awt Dimension Color))
  (:use clojure-crawl.actors
	clojure-crawl.races
	clojure-crawl.classes
	clojure-crawl.utils
	clojure-crawl.mapcanvas
	clojure-crawl.guiutils)
  (:require [clojure-crawl [game :as game]]
	    [clojure-crawl [levels :as levels]]))

;(set! *warn-on-reflection* true)

;; set default look and feel
(UIManager/setLookAndFeel (UIManager/getSystemLookAndFeelClassName))

(defrecord Gui-player [name race clazz strength agility health magic exp life mana level attack defense critical evade life-regen mana-regen hide act-skills all-skills equip bag])

(defrecord Gui-enemy [name description strength agility health magic life mana level attack defense critical evade aware skills])

(defrecord Gui-treasure [name description type kind effect])

;; variables
(def ^JFrame gameframe (new JFrame "Clojure Crawler"))

(def ^JTabbedPane gamepanel (new JTabbedPane))

(def ^JPanel newpanel (new JPanel))

(def ^JComponent mapcanvas (create-map-canvas))

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
		    (new JLabel) (new JLabel) (new JLabel) (new JLabel) (new JLabel)
		    (new JLabel)))

(def gui-treasure (new Gui-treasure (new JLabel) (new JLabel) (new JLabel) (new JLabel)
		       (new JLabel)))

;; helpers
(defn- repaint-map []
  (. mapcanvas repaint))

(defn- status-print [s]
  (. statuslabel setText s))

(defn- gui-go [side]
  (if (game/can-go? side)
    (do (game/go side)
	(repaint-map))
    (status-print (str "Cannot go " (key->name side)))))

;; functions
(defn- set-gui-player []
  (if-let [player @(:player game/game)]
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
  (when-let [enemy (:enemy @(:current-room game/game))]
    (if (dead? enemy)
      (reset-gui-enemy)
      (do
	(set-texts [(:name gui-enemy) (:name enemy)
		    (:description gui-enemy) (:description enemy)
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

(defn- gui-enemy-turn [])

(defn- gui-attack []
  (let [enemy (game/current-enemy)]
    (if (and enemy (not (dead? enemy)))
      (let [res (game/attack-enemy)]
	(if res
	  (if (dead? (game/current-enemy))
	    (status-print (attack->str res))
	    (let [res2 (game/attack-player)]
	      (status-print (str "player: " (attack->str res)
				 " | enemy: " (attack->str res2)))))
	  (status-print "No enemy to attack"))
	(when (dead? (game/player))
	  (game-over))
	(set-gui-player)
	(set-gui-enemy)
	(repaint-map))
      (status-print "No enemy in the room"))))

(defn- gui-skill [name]
  (let [enemy (game/current-enemy)]
    (if (and enemy (not (dead? enemy)))
      (let [res (game/use-skill name)]
	(if res
	  (status-print (str name " - " (attack->str res)))
	  (status-print "Not enough mana"))
	(set-gui-player)
	(set-gui-enemy)
	(repaint-map))
      (status-print "No enemy in the room"))))

(defn- ^JPanel create-all-skills-panel []
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

(defn- ^JPanel create-playerpanel []
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

(defn- ^JPanel create-enemypanel []
  (let [^JPanel panel (new JPanel)
        labels ["Name:" "Description:" "Strength:" "Agility:" "Health:" "Magic:"
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

(defn- ^JPanel create-actionpanel []
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
      (. setSize (new Dimension 250 220))
      (. setBorder border)
      (. setLayout nil))
    panel))

(defn- ^JPanel create-statusbar []
  (doto statusbar
    (. setLayout nil)
    (. setSize 800 25)
    (. add statuslabel))
  (set-bounds [statuslabel [0 0 790 25]]))

(defn- create-gamepanel []
  (let [playerpanel (create-playerpanel)
        enemypanel (create-enemypanel)
        skills (create-all-skills-panel)
        playertab (new JPanel)
        gametab (new JPanel)
	actionpanel (create-actionpanel)
        ^JButton front (new JButton "Front")
        ^JButton rear (new JButton "Rear")
        ^JButton left (new JButton "Left")
        ^JButton right (new JButton "Right")
        ^JButton up (new JButton "Ascend")
        ^JButton down (new JButton "Descend")]
    (set-gui-player)
    (. skills setLocation 0 380)
    (. mapcanvas setLocation 300 10)
    (. enemypanel setLocation 0 380)
    (. actionpanel setLocation 270 480)
    (. gamepanel addChangeListener
      (change-listener (let [pane (. e getSource)
                             name (. pane getTitleAt (. pane getSelectedIndex))]
                         (if (= name "Game")
                           (. gametab add playerpanel)
                           (. playertab add playerpanel)))))
    (set-bounds [front [480 420 70 25]
                 rear [480 450 70 25]
                 left [400 450 70 25]
                 right [560 450 70 25]
                 up [370 420 90 25]
                 down [570 420 90 25]])
    (add-action-listener front (do
                                 (gui-go :front)
				 (status-print "")
                                 (set-gui-enemy)))
    (add-action-listener rear (do
                                (gui-go :rear)
				(status-print "")
                                (set-gui-enemy)))
    (add-action-listener left (do
                                (gui-go :left)
				(status-print "")
                                (set-gui-enemy)))
    (add-action-listener right (do
                                 (gui-go :right)
				 (status-print "")
                                 (set-gui-enemy)))
    (add-action-listener up (do
			      (game/ascend)
			      (status-print "")
			      (repaint-map)))
    (add-action-listener down (do
				(game/descend)
				(status-print "")
				(repaint-map)))
    (add-all gametab front rear left right up down mapcanvas enemypanel
	     actionpanel)
    (doto gamepanel
      (. addTab "Game" gametab)
      (. addTab "Player" playertab)
      (. setSize 800 730))
    (doto gametab
      (. setLayout nil)
      (. setSize 800 710))
    (doto playertab
      (. setLayout nil)
      (. setSize 800 710)
      (. add skills))))

(defn- goto-game [pname race clazz]
  (let [^JPanel panel (new JPanel)]
    (game/start-game pname race clazz)
    (set-gui-player)
    (set-bounds [panel [0 20 800 780]])
    (. gamepanel setLocation 0 0)
    (. statusbar setLocation 0 730)
    (doto panel
      (. setLayout nil)
      (. add gamepanel)
      (. add statusbar))
    (. gameframe setContentPane panel)))

(defn- init-menu []
  (let [mainmenu (new JMenuBar)
        gamemenu (new JMenu "game")
        saveitem (new JMenuItem "save")
        loaditem (new JMenuItem "load")
        exititem (new JMenuItem "exit")
        helpmenu (new JMenu "help")
        aboutitem (new JMenuItem "about")]
    (. gamemenu add saveitem)
    (. gamemenu add loaditem)
    (. gamemenu add exititem)
    (. helpmenu add aboutitem)
    (. aboutitem addActionListener
       (action-listener
	(show-message gameframe "Clojure Crawler - a dungeon crawler made in clojure.
Source released under Eclipse License.
http://github.com/stackoverflow/clojure-crawl"
		      "Clojure Crawler")))
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