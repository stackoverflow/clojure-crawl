(ns clojure-crawl.gui
  (:import (javax.swing JFrame JMenuBar JMenu UIManager
			JMenuItem JOptionPane)
	   (java.awt.event ActionListener)))

;; set default look and feel
(UIManager/setLookAndFeel (UIManager/getSystemLookAndFeelClassName))

;; helpers
(defn action-listener [f]
  (proxy [ActionListener] []
    (actionPerformed [e]
		     (f e))))

(def gameframe (new JFrame "Clojure Crawler"))

(defn set-menu []
  (let [mainmenu (new JMenuBar)
	gamemenu (new JMenu "game")
	saveitem (new JMenuItem "save")
	loaditem (new JMenuItem "load")
	helpmenu (new JMenu "help")
	aboutitem (new JMenuItem "about")]
    (. gamemenu add saveitem)
    (. gamemenu add loaditem)
    (. helpmenu add aboutitem)
    (. aboutitem addActionListener
       (action-listener
	(fn [e] (JOptionPane/showMessageDialog gameframe "funfou" "test"
					       JOptionPane/INFORMATION_MESSAGE))))
    (. mainmenu add gamemenu)
    (. mainmenu add helpmenu)
    (. gameframe setJMenuBar mainmenu)))