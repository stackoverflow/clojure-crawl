(ns clojure-crawl.guiutils
  (:import (javax.swing JOptionPane JComponent JLabel)
	   (java.awt.event ActionListener ActionEvent)
	   (javax.swing.event ListSelectionListener
			      ListSelectionEvent
			      ChangeListener ChangeEvent)
	   (java.awt Dimension Component)))

(defn show-message [parent msg title]
  (JOptionPane/showMessageDialog parent msg title JOptionPane/INFORMATION_MESSAGE))

(defmacro list-selection-listener [body]
  `(proxy [ListSelectionListener] []
     (valueChanged [^ListSelectionEvent ~'e]
	      ~body)))

(defmacro add-action-listener [c body]
  `(. ~c addActionListener (action-listener ~body)))

(defmacro action-listener [body]
  `(proxy [ActionListener] []
     (actionPerformed [^ActionEvent ~'e]
		      ~body)))

(defmacro change-listener [body]
  `(proxy [ChangeListener] []
     (stateChanged [^ChangeEvent ~'e]
		   ~body)))

(defn set-bounds [items]
  (when (even? (count items))
    (let [s (vec (partition 2 items))]
      (doseq [[^Component c [x y w h]] s]
	(. c setBounds x y w h)))))

(defn add-all [^JComponent component & args]
  (doseq [c args]
    (. component add c)))

(defn set-texts [coll]
  (when (even? (count coll))
    (let [s (partition 2 coll)]
      (doseq [[l ^String text] s]
	(. l setText text)))))