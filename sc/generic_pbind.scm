(let ((inst (quote "\\snare")))

;; Include repeat_list
(define source_list (list 0.5 0.25 0.125 0.0625))
(define reps 4)
(define function "Pseq")
(define param "\\dur")

(define (pseries lst i)
  (let ((max (length lst)))
    (when (< i max)
      (if (= i (- max 1))
	  (format #t "~a ], inf)" (list-ref lst i))
	  (format #t "~a, " (list-ref lst i)))
      (pseries lst (1+ i)))))

(define (repeat n lst)
  (format #t "~a, ~a([" param function)
  (map (lambda (x) (make-list (* (inexact->exact (floor (/ 1 x))) n) x)) lst))

;; Pbind definition to output with instrument
(display "Pbind(\\instrument, ")
(format #t "~a,~&" inst)

;; Add data to it
(pseries (apply append (repeat 4 '(1 0.5 0.25))) 0)

;; Close the Pbind
(display ").play")
)
