(ns clojure-crawl.gui
  (:import (javax.swing JFrame JMenuBar JMenu UIManager
			JMenuItem JOptionPane JPanel JLabel
			JList JTextField JTextArea JButton
			JScrollPane ListSelectionModel)
	   (javax.swing.event ListSelectionListener
			      ListSelectionEvent)
	   (java.awt.event ActionListener ActionEvent)
	   (java.awt Dimension Color Component))
  (:use clojure-crawl.races
	clojure-crawl.classes
	clojure-crawl.game))

(set! *warn-on-reflection* true)

;; set default look and feel
(UIManager/setLookAndFeel (UIManager/getSystemLookAndFeelClassName))

;; variables
(def ^JFrame gameframe (new JFrame "Clojure Crawler"))

(def ^JPanel gamepanel (new JPanel))

(def ^JPanel newpanel (new JPanel))

(defrecord Gui-player [clazz strength agility health magic act-skills all-skills exp life max-life mana max-mana level attack defense critical evade life-regen mana-regen hide equip bag])

(defrecord Gui-enemy [name description strength agility health magic skills life max-life mana max-mana level attack defense critical evade])

(defrecord Gui-treasure [name description type kind effect])

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

;; functions
(defn create-gamepanel [])
  

(defn- goto-game [pname race clazz]
  (start-game pname race clazz)
  (create-gamepanel)
  (. gameframe setContentPane gameframe))

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

(defn- set-bounds [items]
  (when (even? (count items))
    (let [s (vec (partition 2 items))]
      (doseq [[^Component c [x y w h]] s]
	(. c setBounds x y w h)))))

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
  (. gameframe setMinimumSize (new Dimension 800 700))
  (. gameframe setResizable false)
  (. gameframe setVisible true))