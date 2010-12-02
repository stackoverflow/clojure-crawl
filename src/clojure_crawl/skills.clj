(ns clojure-crawl.skills
  (:import (clojure-crawl.actors Skill))
  (:use clojure-crawl.actors
	clojure-crawl.utils))

(extend-type Skill
  Descriptable
  (describe [skill]
	  (describe-effect (:effect skill))))

(defmacro gen-skill-methods [name consu st ag he ma at de cr ev li mn lr mr hi]
  `(do (defmethod ~'mana-consume ~name [~'skill ~'actor] ~consu)
       (defmethod ~'skill-strength ~name [~'skill ~'actor] ~st)
       (defmethod ~'skill-agility ~name [~'skill ~'actor] ~ag)
       (defmethod ~'skill-health ~name [~'skill ~'actor] ~he)
       (defmethod ~'skill-magic ~name [~'skill ~'actor] ~ma)
       (defmethod ~'skill-attack ~name [~'skill ~'actor] ~at)
       (defmethod ~'skill-defense ~name [~'skill ~'actor] ~de)
       (defmethod ~'skill-critical ~name [~'skill ~'actor] ~cr)
       (defmethod ~'skill-evade ~name [~'skill ~'actor] ~ev)
       (defmethod ~'skill-life ~name [~'skill ~'actor] ~li)
       (defmethod ~'skill-mana ~name [~'skill ~'actor] ~mn)
       (defmethod ~'skill-life-regen ~name [~'skill ~'actor] ~lr)
       (defmethod ~'skill-mana-regen ~name [~'skill ~'actor] ~mr)
       (defmethod ~'skill-hide ~name [~'skill ~'actor] ~hi)))

(def *skills*
     {:crush (new Skill "Crush" "Powerfull attack" true true (atom 1) :enemy)
      :critical-strike (new Skill "Critical Strike"
			    "Improves attack and critical %" true true (atom 1) :enemy)
      :fireball (new Skill "Fireball" "Auto-descriptive" true true (atom 1) :enemy)
      :heal (new Skill "Heal" "Increase current life" false true (atom 1) :self)
      :attack-up (new Skill "Attack Up" "Increases attack power"
		      false false nil nil)
      :sneak (new Skill "Sneak" "Increases hide %" false false nil nil)
      :magic-up (new Skill "Magic Up" "Increases magic power"
		     false false nil nil)
      :vitality (new Skill "Vitality" "Increases life" false false nil nil)})

(gen-skill-methods "Crush"
		   (* 5 @(:level skill)) ;consume
		   nil nil nil nil
		   (let [bonus (+ 1 (/ @(:level skill) 10))]
		     (+ 10 (* bonus (attack actor)))) ;attack
		   nil nil nil nil nil nil nil nil)

(gen-skill-methods "Critical Strike"
		   (* 5 @(:level skill)) ;consume
		   nil nil nil nil
		   (+ (* 5 @(:level skill)) (attack actor)) ;attack
		   nil
		   (+ (* 5 @(:level skill)) (critical actor)) ;critical
		   nil nil nil nil nil nil)

(gen-skill-methods "Fireball"
		   (* 5 @(:level skill)) ;consume
		   nil nil nil nil
		   (let [mag @(:magic actor)
			 m3 (* mag 0.75)			 
			 bonus (+ 1 (/ @(:level skill) 10))]
		     (* bonus (rand-between m3 mag))) ;attack (magic)
		   nil nil nil nil nil nil nil nil)

(gen-skill-methods "Heal"
		   (* 5 @(:level skill)) ;consume
		   nil nil nil nil
		   (let [mag @(:magic actor)
			 m3 (* mag 0.75)			 
			 bonus (+ 1 (/ @(:level skill) 10))]
		     (* bonus (rand-between m3 mag))) ;attack (magic)
		   nil nil nil nil nil nil nil nil)

(gen-skill-methods "Attack Up"
		   nil nil nil nil nil
		   @(:level actor) ;attack
		   nil nil nil nil nil nil nil nil)

(gen-skill-methods "Sneak"
		   nil nil nil nil nil nil nil nil nil nil nil nil nil
		   (/ @(:level actor) 2)) ;hide

(gen-skill-methods "Magic Up"
		   nil nil nil nil
		   @(:level actor) ;magic
		   nil nil nil nil nil nil nil nil nil)

(gen-skill-methods "Vitality Up"
		   nil nil nil nil nil nil nil nil nil
		   (* 3 @(:level actor)) ;health
		   nil nil nil nil)