(ns clojure-crawl.mapcanvas
  (:use clojure-crawl.map
	clojure-crawl.actors)
  (:import (javax.swing JPanel)
	   (java.awt Graphics Color Font FontMetrics)))

(set! *warn-on-reflection* true)

(def ^{:private true} sep 3)

(def ^{:private true} treasure-color (new Color 180 180 0))

(def ^{:private true} font (new Font "SanSerif" Font/PLAIN 12))

(defn- ^Integer height [level asc]
  (int (+ (* level sep) (* level asc))))

(defn- gen-legend [^Graphics g ^FontMetrics fm asc]
  (let [words ["player, " " enemy, " " treasure, "
               " shrine, " " up, " " down"]
        colors [Color/blue Color/red treasure-color
                Color/green Color/magenta Color/gray]
        h (height 1 asc)
	initx (. fm stringWidth "Legend: ")]
    (. g setColor Color/white)
    (. g drawString "Legend: " 5 h)
    (loop [w words, x (+ 5 initx), c colors]
      (when w
        (let [^String word (first w)
              ^Color color (first c)
              size (. fm stringWidth word)]
          (. g setColor color)
          (. g fillRect x (- h 8) 10 10)
	  (. g setColor Color/white)
          (. g drawString word (+ x 10 sep) h)
          (recur (next w) (+ x size sep sep 10) (next c)))))))

(defn- draw-map [^Graphics g w h]
  (. g setFont font)
  (let [level @current-level
	fm (. g getFontMetrics)
	asc (. fm getAscent)
	wh 35
	startx 5
	starty 400
	gap 5]
    (. g setColor Color/black)
    (. g fillRect 0 0 w, h)
    (. g setColor Color/white)
    (. g drawString (str "Level: " (:depth level)) (- w 60) (height 1 asc))
    (gen-legend g fm asc)
    (. g setColor Color/white)
    (loop [rooms (:rooms @current-level)]
      (when rooms
	(let [r (first rooms)
	      pos (:pos r)
	      postmp (mod pos size)
	      posrel (if (= postmp 0) size postmp)
	      x (+ (* posrel (+ wh gap)) startx)
	      y (- starty (* (+ wh gap) (int (/ (dec pos) size))))]
	  (when true
	    (. g fillRect x y wh wh)
	    (when (= pos (:pos @current-room))
	      (. g setColor Color/blue)
	      (. g fillRect (+ x (/ wh 2) -5) (+ y (/ wh 2) -5) 10 10))
	    (when (and (:enemy r)
		       (not (dead? (:enemy r))))
	      (. g setColor Color/red)
	      (. g fillRect x y 10 10))
	    (when @(:treasure r)
	      (. g setColor treasure-color)
	      (. g fillRect (+ x (- wh 10)) y 10 10))
	    (when (:up? r)
	      (. g setColor Color/magenta)
	      (. g fillRect x (+ y (- wh 10)) 10 10))
	    (when (:down? r)
	      (. g setColor Color/gray)
	      (. g fillRect x (+ y (- wh 10)) 10 10))
	    (when (:shrine? r)
	      (. g setColor Color/green)
	      (. g fillRect x (+ y (- wh 10)) 10 10))
	    (. g setColor Color/white)
	    (when (:right r)
	      (. g fillRect (+ x wh) (+ y 12) gap (/ wh 3)))
	    (when (:front r)
	      (. g fillRect (+ x 12) (- y gap) (/ wh 3) gap)))
	  (recur (next rooms)))))))

(defn ^JPanel create-map-canvas []
  (let [c (proxy [JPanel] []
	    (paintComponent [^Graphics g]
			    (let [w (. ^JComponent this getWidth)
				  h (. ^JComponent this getHeight)]
			      (proxy-super paintComponent g)
			      (draw-map g w h))))]
    (. c setSize 500 450)
    c))