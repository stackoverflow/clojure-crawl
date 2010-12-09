(ns clojure-crawl.races
  (:use clojure-crawl.things)
  (:import (clojure-crawl.actors Race)
	   (clojure-crawl.things Effect)))

(def *races*
     {:human (new Race "Human" "Standard humam beings"
		  (new Effect 3 3 3 3 0 0 0 0 0 0 0 0 0 nil nil))
      :elf (new Race "Elf" "Point-eared immortals with the gift of magic"
		(new Effect 0 4 0 4 0 0 0 0 0 10 0 0 0 nil nil))
      :dwarf (new Race "Dwarf" "Short and bearded people who live in caves"
		  (new Effect 4 0 4 0 0 0 0 0 15 0 0 0 0 nil nil))
      :orc (new Race "Orc" "Ugly green-skinned and strong"
		(new Effect 5 0 3 0 5 0 0 0 0 0 0 0 0 nil nil))
      :hobbit (new Race "Hobbit" "Small and sneaky"
		   (new Effect 0 5 0 0 0 0 1 1 0 0 0 0 5 nil nil))
      :construct (new Race "Construct" "Magic made creatures"
		      (new Effect 3 0 5 0 0 0 0 0 0 0 4 0 0 nil nil))
      :djinn (new Race "Djinn" "Masters of elements"
		  (new Effect 0 3 0 5 0 0 0 0 0 0 0 9 0 nil nil))
      :vampire (new Race "Vampire" "Hideous blood suckers"
		    (new Effect 0 0 0 0 0 5 0 0 0 0 4 0 5 nil nil))})

(defn race-names []
  (map :name (vals *races*)))

(defn show-race [name]
  (let [race (first (filter #(= (:name %) name) (vals *races*)))]
    (str "race: " (:name race) "\n"
	 (:description race) "\n"
	 (show-effect (:effect race)))))