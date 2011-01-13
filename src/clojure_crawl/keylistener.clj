(ns clojure-crawl.keylistener
  (:import (java.awt.event KeyListener KeyEvent)))

(def ^{:private true} pressed (atom {}))
(def ^{:private true} released (atom {}))
(def ^{:private true} typed (atom {}))

(defn- upper [s]
  (.toUpperCase s))

(defn- add [var k f]
  (swap! var assoc k f))

(defn- check-key [e var]
  (let [mods (KeyEvent/getKeyModifiersText (.getModifiersEx e))
	key (KeyEvent/getKeyText (.getKeyCode e))
	modkey (if (and mods (not (empty? mods)))
		 (upper (str mods "+" key))
		 (upper key))]
    (doseq [[k f] @var]
      (let [uk (upper (subs (str k) 1))]
	(when (= uk modkey)
	  (f))))))

(defn add-key-pressed [k f]
  (add pressed k f))

(defn add-key-released [k f]
  (add released k f))

(defn add-key-typed [k f]
  (add typed k f))

(defn key-listener []
  (proxy [KeyListener] []
    (keyPressed [^KeyEvent e]
		(check-key e pressed))
    (keyReleased [^KeyEvent e]
		 (check-key e released))
    (keyTyped [^KeyEvent e]
	      (check-key e typed))))