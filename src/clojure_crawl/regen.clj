(ns clojure-crawl.regen
  (:require [clojure-crawl [game :as game]]
	    [clojure-crawl [actors :as actors]]))

(def ^{:private true} function (atom nil))

(def ^{:private true} ison (atom false))

(defn set-repaint [f]
  (reset! function f))

(defn on []
  (reset! ison true))

(defn off []
  (reset! ison false))

(defn- execute []
  (when @ison
    (when-let [player (game/player)]
      (actors/regen-life player)
      (actors/regen-mana player)
      (when @function
	(@function)))))

(defn- timefun []
  (Thread/sleep 1000)
  (execute)
  (recur))

(def ^{:private true} thread (new Thread timefun))

(defn start []
  (when (not (.isAlive thread))
    (.start thread)))