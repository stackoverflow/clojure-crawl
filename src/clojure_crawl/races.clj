(ns clojure-crawl.races
  (:use clojure-crawl.actors
	clojure-crawl.things))

(def *races*
     [(new Race "Human" "You know these guys..."
	   (new Effect 3 3 3 3 0 0 0 0 0 0 0 0 0 nil nil))
      (new Race "Elf" "Point-eared immortals with the gift of magic"
	   (new Effect 0 4 0 4 0 0 0 0 0 10 0 0 0 nil nil))
      (new Race "Dwarf" "Short bearded people who live in caves"
	   (new Effect 4 0 4 0 0 0 0 0 15 0 0 0 0 nil nil))
      (new Race "Orc" "Ugly green-skined and strong"
	   (new Effect 5 0 3 0 5 0 0 0 0 0 0 0 0 nil nil))
      (new Race "Hobbit" "Small and sneaky"
	   (new Effect 0 5 0 0 0 0 1 1 0 0 0 0 5 nil nil))
      (new Race "Construct" "Magic made creatures"
	   (new Effect 3 0 5 0 0 0 0 0 0 0 4 0 0 nil nil))
      (new Race "Djinn" "Masters of elements"
	   (new Effect 0 3 0 5 0 0 0 0 0 0 0 9 0 nil nil))
      (new Race "Vampire" "Hideous blood suckers"
	   (new Effect 0 0 0 0 0 5 0 0 0 0 4 0 5 nil nil))])