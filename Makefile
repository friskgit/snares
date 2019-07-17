faustdir 	:= ./faust/snare/bin
convolver_file	:= ~/Dropbox/Documents/kmh/dome/new_convolver_presets/kmh_lilla_salen/KMH_LILLA_SALEN.conf

test :
	~/bin/jdis -c

convolver :
	jconvolver_static_0.9.2.2 $(convolver_file)
