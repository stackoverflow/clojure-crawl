(ns clojure-crawl.classes
  (:import (clojure-crawl.actors Clazz)))

(def *classes*
     {:warrior (new Clazz "Warrior" "Fighter specialized in melee damage" 4 2 3 2 [:crush :attack-up])
      :rogue (new Clazz "Rogue" "Sneaky warrior specialized in critical, evade and hide"
		  3 4 2 2 [:critical-strike :sneak])
      :mage (new Clazz "Mage" "Master of magic with low damage and life" 2 3 2 4 [:fireball :magic-up])
      :cleric (new Clazz "Cleric" "Holy man with divine magic. Highly durable" 1 1 4 3 [:heal :vitality])})