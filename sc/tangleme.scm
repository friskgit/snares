(let ((inst (quote "\\snare")))

;; Include repeat_list
(define (pseries lst i)
  (let ((max (length lst)))
    (when (< i max)
      (if (= i (- max 1))
	  (format #t "~a ], inf)" (list-ref lst i))
	  (format #t "~a, " (list-ref lst i)))
      (pseries lst (1+ i)))))

(define (repeat n lst)
  (map (lambda (x) (make-list (* (inexact->exact (floor (/ 1 x))) n) x)) lst))

;; Pbind definition to output with instrument
(display "Pbind(\\instrument, ")
(format #t "~a," inst)

;; Repeat the three statements below this for more parameters
;; Add a parameter to the EventList
(format #t "~a, " "\\dur")
(format #t "~a([" "Pseq")
;; Add data to it
(pseries (apply append (repeat 4 '(1 0.5 0.25))) 0)

;; Close the Pbind
(display ").play")
)
