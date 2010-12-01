(ns clojure-crawl.classes
  (:import (clojure-crawl.actors Skill Descriptable))
  (:use clojure-crawl.actors))

(extend-type Skill
  Descriptable
  (describe [skill]
	    (describe-effect (:effect skill))))

(def *skills*
     {:crush (new Skill "Crush" "Powerfull attack" true true (atom 1) :enemy)
      :critical-strike (new Skill "Critical Strike"
			    "Improves attack and critical %" true true (atom 1) :enemy)
      :fireball (new Skill "Fireball" "Auto-descritive" true true (atom 1) :enemy)
      :heal (new Skill "Heal" "Increase current life" false true (atom 1) :self)
      :attack-up (new Skill "Attack Up" "Increases attack power"
		      false false nil nil)
      :sneak (new Skill "Sneak" "Increases hide %" false false nil nil)
      :magic-up (new Skill "Magic Up" "Increases magic power"
		     false false nil nil)
      :vitality (new Skill "Vitality" "Increases life" false false nil nil)})