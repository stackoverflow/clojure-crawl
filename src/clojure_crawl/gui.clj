(ns clojure-crawl.gui
  (:import (javax.swing JFrame JMenuBar JMenu UIManager
			JMenuItem JOptionPane JPanel JLabel
			JList JTextField JTextArea JButton
			JScrollPane ListSelectionModel
			BorderFactory JTabbedPane)
	   (javax.swing.event ListSelectionListener
			      ListSelectionEvent)
	   (javax.swing.border TitledBorder)
	   (java.awt.event ActionListener ActionEvent)
	   (java.awt Dimension Color Component))
  (:use clojure-crawl.actors
	clojure-crawl.races
	clojure-crawl.classes
	clojure-crawl.game
	clojure-crawl.utils))

;(set! *warn-on-reflection* true)

;; set default look and feel
(UIManager/setLookAndFeel (UIManager/getSystemLookAndFeelClassName))

(defrecord Gui-player [name race clazz strength agility health magic exp life mana level attack defense critical evade life-regen mana-regen hide act-skills all-skills equip bag])

(defrecord Gui-enemy [name description strength agility health magic skills life mana level attack defense critical evade])

(defrecord Gui-treasure [name description type kind effect])

;; variables
(def ^JFrame gameframe (new JFrame "Clojure Crawler"))

(def ^JPanel gamepanel (new JTabbedPane))

(def ^JPanel newpanel (new JPanel))

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

(def gui-treasure (new Gui-treasure (new JLabel) (new JLabel) (new JLabel) (new JLabel)
		       (new JLabel)))

;; helpers
(defn- show-message [msg title]
  (JOptionPane/showMessageDialog gameframe msg title JOptionPane/INFORMATION_MESSAGE))

(defn- action-listener [f]
  (proxy [ActionListener] []
    (actionPerformed [^ActionEvent e]
		     (f e))))

(defn- list-selection-listener [f]
  (proxy [ListSelectionListener] []
    (valueChanged [^ListSelectionEvent e]
		  (f e))))

(defn- set-bounds [items]
  (when (even? (count items))
    (let [s (vec (partition 2 items))]
      (doseq [[^Component c [x y w h]] s]
	(. c setBounds x y w h)))))

;; functions
(defn- set-gui-player []
  (let [player @(:player game)
	skills @(:skills player)
	[list _] (:all-skills gui-player)]
    (. (:name gui-player) setText (:name player))
    (. (:race gui-player) setText (:name (:race player)))
    (. (:clazz gui-player) setText (:name @(:clazz player)))
    (. (:strength gui-player) setText (str (strength player)))
    (. (:agility gui-player) setText (str (agility player)))
    (. (:health gui-player) setText (str (health player)))
    (. (:magic gui-player) setText (str (magic player)))
    (. (:exp gui-player) setText (str @(:exp player)))
    (. (:life gui-player) setText (str @(:life player) "/" (max-life player)))
    (. (:mana gui-player) setText (str @(:mana player) "/" (max-mana player)))
    (. (:level gui-player) setText (str @(:level player)))
    (. (:attack gui-player) setText (vec->damage (base-attack player)))
    (. (:defense gui-player) setText (str (to-num (defense player))))
    (. (:critical gui-player) setText (str (to-num (critical player)) "%"))
    (. (:evade gui-player) setText (str (to-num (evade player)) "%"))
    (. (:life-regen gui-player) setText (str (to-num (life-regen player)) " per second"))
    (. (:mana-regen gui-player) setText (str (to-num (mana-regen player)) " per second"))
    (. (:hide gui-player) setText (str (to-num (hide player)) "%"))
    (. list setListData (object-array (map :name skills)))))

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
       (list-selection-listener (fn [e]
				  (let [name (. jlist getSelectedValue)]
				    (. tarea setText (show-skill name @(:player game)))))))
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
      (. setSize (new Dimension 200 360))
      (. setBorder border)
      (. setLayout nil))
    (doseq [[^String l ^JPanel v] sequ]
      (let [^JLabel label (new JLabel l)]
	(. label setBounds 10 @y 70 18)
	(. v setBounds 100 @y 80 18)
	(. panel add label)
	(. panel add v)
	(swap! y + 19)))
    panel))

(defn- create-gamepanel []
  (let [playerpanel (create-playerpanel)
	skills (create-all-skills-panel)
	playertab (new JPanel)
	gametab (new JPanel)]
    (set-gui-player)
    (. skills setLocation 0 380)
    (doto gamepanel
      (. addTab "Game" gametab)
      (. addTab "Player" playertab)
      (. setBounds 0 20 800 800))
    (doto gametab
      (. setSize 800 800)
      (. setLayout nil)
      (. add playerpanel))
    (doto playertab
      (. setSize 800 800)
      (. setLayout nil)
      (. add playerpanel)
      (. add skills))))
  

(defn- goto-game [pname race clazz]
  (start-game pname race clazz)
  (create-gamepanel)
  (. gameframe setContentPane gamepanel)
  (. gameframe invalidate))

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
	(fn [e] (show-message "Clojure Crawler - a dungeon crawler made in clojure.
Source released under Eclipse License.
http://github.com/stackoverflow/clojure-crawl"
			      "Clojure Crawler"))))
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
       (list-selection-listener (fn [e]
				  (let [name (. racelist getSelectedValue)]
				    (. racedesc setText (show-race name))))))
    (. classlist addListSelectionListener
       (list-selection-listener (fn [e]
				  (let [name (. classlist getSelectedValue)]
				    (. classdesc setText (show-class name))))))
    (. newbutton addActionListener
       (action-listener (fn [e]
			  (let [^String name (. namefield getText)
				^String race (. racelist getSelectedValue)
				^String clazz (. classlist getSelectedValue)]
			    (if (. name isEmpty)
			      (show-message "Please insert a name" "Error")
			      (if (or (not race)
				      (. race isEmpty))
				(show-message "Please choose a race" "Error")
				(if (or (not clazz)
					(. clazz isEmpty))
				  (show-message "Please choose a class" "Error")
				  (goto-game name race clazz))))))))
    ;; add
    (doto newpanel
      (. setLayout nil)
      (. add racelabel)
      (. add racedesclabel)
      (. add classlabel)
      (. add classdesclabel)
      (. add namelabel)
      (. add newbutton)
      (. add racelistsc)
      (. add racedescsc)
      (. add classlistsc)
      (. add namefield)
      (. add classdescsc))))

(defn init-gui []
  (init-menu)
  (create-newpanel)
  (. gameframe setContentPane newpanel)
  (. gameframe setMinimumSize (new Dimension 800 800))
  (. gameframe setResizable false)
  (. gameframe setVisible true))