(ns clojure-crawl.actors)

(defrecord Race [name description])

(defrecord Player [name race clazz strength agility health magic skills exp life max-life mana max-mana level equip bag])

(defrecord Enemy [name description clazz strength agility health magic skills life max-life mana max-mana level])

(defprotocol )