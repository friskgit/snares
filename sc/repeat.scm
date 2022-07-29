(define (pseries lst i)
  (let ((max (length lst)))
    (when (< i max)
      (if (= i (- max 1))
	  (format #t "~a ], inf)" (list-ref lst i))
	  (format #t "~a, " (list-ref lst i)))
      (pseries lst (1+ i)))))

(define (repeat n lst)
  (map (lambda (x) (make-list (* (inexact->exact (floor (/ 1 x))) n) x)) lst))

(format #t "~a, " param)
(format #t "~a([" function)
(pseries (apply append (repeat reps source_list)) 0)
