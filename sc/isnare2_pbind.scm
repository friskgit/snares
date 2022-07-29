(let ((inst (quote "\\isnare2")))
;; Include repeat_list

(define (print-list i lst)
  (when (< i (length lst))
    (format #t "~a,~&" (list-ref lst i))
    (print-list (1+ i) lst)))

(define attributes (list (cons "group" "~group")
			 (cons "addAction" 1)
			 (cons "position" 0)
			 (cons "disperse" 0)
			 (cons "noise" 0)
			 (cons "freq" 1)
			 (cons "dur" 1)
			 (cons "inBus1" "~saw_control_bus_1.index")
			 (cons "inBus2" "~saw_control_bus_2.index")
			 (cons "inBus3" "~saw_control_bus_3.index")))

;; Instantiate the group and the control instruments
(display "~group = Group.new; ~freq_ctrl = Synth(\\control_saw2, [\\bus, ~saw_control_bus_1.index, \\freq, 1, \\mult, 1, \\add, 1], ~group, \\addToHead); ~freq_ctrl2 = Synth(\\control_saw2, [\\bus, ~saw_control_bus_2.index, \\freq, 1, \\mult, 1, \\add, 1], ~group, \\addToHead); ~impulse_ctrl = Synth(\\control_saw2, [\\bus, ~saw_control_bus_3.index, \\freq, 0.5, \\mult, 1, \\add, 1], ~group, \\addToHead);")
(display "Pbind(\\instrument, ")
(format #t "~a,~&" inst)

(print-list 0 (map 
	       (lambda (x) 
		 (format #f "\\~a, ~a" (car x) (cdr x))) attributes))

;; Repeat the four statements below this for more parameters
;; Add a parameter to the EventList

;; Close the Pbind
(display ").play")
)
