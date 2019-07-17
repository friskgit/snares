# Makefile for faust code compilation.
# 
# This file assumes that your faust code exists in
# a subdirectory at the same hieararchy as this named
# 'src'. It may contain one or several .dsp files.
# These files should be specified below in the variable
# 'input'.
#
# The clean directory looks like this:
#
# ./Makefile
# ./src
#
# and the build process addes the 'bin' and 'doc' directories.
#
# The targets, apart from 'all' are:
#
# * jack
# * app
# * runapp (will run the application)
# * max
# * sc (supercollider)
# * pd
# * mdoc
# * html
# * oct
# * octview (will run octave with the current file)
# * svg 
# * clean
# * cleanbin (will only clean the bin directory)
#
# 30 Juni 2018	Henrik Frisk	mail@henrikfrisk.com

## Specify the sources to build here
srcdir	:= src
src	:= $(wildcard $(addsuffix /*.dsp, $(srcdir)))
## src	:= $(srcdir)/snare.dsp

## Output directories, dipsosable
bindir	:= bin
docdir	:= doc
cppdir 	:= cpp
scdir	:= $(bindir)/sc
maxdir	:= $(bindir)/max
vstdir	:= $(bindir)/vst
pddir	:= $(bindir)/pd

## Targets
jack	:= $(addprefix $(bindir)/, $(notdir $(basename $(src))))
app	:= $(addprefix $(bindir)/, $(notdir $(src:.dsp=.app)))
vst	:= $(addprefix $(vstdir)/, $(notdir $(src:.dsp=.vst)))
mxo	:= $(addprefix $(maxdir)/, $(notdir $(src:.dsp=~.mxo)))
sc	:= $(addprefix $(scdir)/, $(notdir $(src:.dsp=.sc)))
scx	:= $(addprefix $(scdir)/, $(notdir $(src:.dsp=.scx)))
superscx	:= $(addprefix $(scdir)/, $(notdir $(src:.dsp=_supernova.scx)))
pd	:= $(addprefix $(pddir)/, $(notdir $(src:.dsp=~.pd_darwin)))
html	:= $(addprefix $(docdir)/, $(notdir $(src:.dsp=.html)))
oct	:= $(addprefix $(bindir)/, $(notdir $(src:.dsp=.m)))
svg	:= $(addprefix $(docdir)/, $(notdir $(src:.dsp=-svg)))
mdoc	:= $(addprefix $(docdir)/, $(notdir $(src:.dsp=-mdoc)))
prgrms	:= $(jack) $(app) $(html) $(mxo) $(sc)

POLY		:= -nvoices 8
HTTPD		:= -httpd
OSC		:= -osc
MIDI		:= -midi

# Any of the above
FLAGS		:= $(OSC) ## $(MIDI) $(POLY)
FX		:= -effect
NO_PATCH	:= -nppatch

# Any of the above except OSC and HTTPD
MAX_FLAGS	:= $(POLY)
FAUST2SVG	:= faust2firefox
FAUST2VST	:= faust2vst
FAUST2PD	:= faust2puredata
FAUST2CAQT	:= faust2caqt
FAUST2JACK	:= faust2jack
FAUST2MAX	:= faust2max6
FAUST2SC	:= faust2supercollider -sn -ks -noprefix
FAUST2OCT 	:= $(HOME)/bin/f2o
FAUST2OCTV 	:= faust2octave
FAUST2MDOC	:= faust2mathdoc
REQUIRED_DIRS	= $(bindir) $(docdir) $(cppdir) $(scdir) $(maxdir) $(vstdir) $(pddir)
MKDIR	       	:= $(shell for d in $(REQUIRED_DIRS); \
			do				\
			[[ -d $$d ]] || mkdir -p $$d; 	\
			done)

SC_INSTALL	:= ~/Library/Application\ Support/SuperCollider/Extensions/Faust
.DEFAULT_GOAL	:= all

all : $(app) $(jack) $(vst) $(html) $(mxo) $(sc) $(svg) $(pd)

run : $(jack)
	./bin/snare

doc : $(html)

jack : $(jack)

app : $(app)

max : $(mxo)

sc : $(sc)

oct : $(oct)

octview : $(src)
	$(FAUST2OCTV) $<

svg : $(svg)
#	inkview $(svg)

pd : $(pd)

vst : $(vst)

mdoc : $(mdoc)

test :
	@echo $(mdoc)

$(vstdir)/%.vst : $(srcdir)/%.dsp
	$(FAUST2VST) $(FLAGS) $<
	@mv $(srcdir)/$(notdir $@) $@

$(bindir)/%.app : $(srcdir)/%.dsp
	@echo compiling $<
	$(FAUST2CAQT) $(FLAGS) $<
	@mv $(srcdir)/$(notdir $@) $@

#### Jack
$(bindir)/% : $(srcdir)/%.dsp
	$(FAUST2JACK) $(FLAGS) $<
	@mv $(srcdir)/$(notdir $@) $@

$(maxdir)/%~.mxo : $(srcdir)/%.dsp
	echo $<
	$(FAUST2MAX) $<
	@mv $(srcdir)/$(notdir $@) $@
	@mv $(notdir $(<:.dsp=.maxpat)) $(maxdir)
	@mv ui.js $(maxdir)

$(pddir)/%~.pd_darwin : $(srcdir)/%.dsp
	$(FAUST2PD) $<
	@mv $(srcdir)/$(notdir $@) $@

doc/%.html : doc/%.md
	pandoc $< -f markdown -t html -o $@

doc/%.md : src/%.dsp
	faust2md $< > $@

$(docdir)/%-mdoc : $(srcdir)/%.dsp
	$(FAUST2MDOC) $<
	@cp -r $(<:.dsp=-mdoc) $@
	@rm -rf $(<:.dsp=-mdoc)

$(bindir)/%.m : $(srcdir)/%.dsp
	$(FAUST2OCT) $<
	@mv $(<:.dsp=.m) $@

$(docdir)/%-svg : $(srcdir)/%.dsp
	$(FAUST2SVG) $<
	@cp -r $(<:.dsp=-svg) $@
	@rm -rf $(<:.dsp=-svg)

$(scdir)/%.sc : $(srcdir)/%.dsp
	export SUPERCOLLIDER_HEADERS=~/bin/supercollider/include/; \
	$(FAUST2SC) $<
	@mv $(basename $<).sc $@
	@mv $(basename $<).scx $(scdir)
	@mv $(basename $<)_supernova.scx $(scdir)
	@mv $(basename $<).cpp $(cppdir)

testme :
	@echo $(superscx)

scinstall : ##$(SC) $(SCX) $(SSCX)
	@echo "Installing SuperCollider extensions in $(SC_INSTALL)"
	@echo "$(sc)"
	@echo "$(scx)"
	@echo "$(superscx)"
	$(foreach var, $(sc), $(shell cp $(var) $(SC_INSTALL)))
	$(foreach var, $(scx), $(shell cp $(var) $(SC_INSTALL)))
	$(foreach var, $(superscx), $(shell cp $(var) $(SC_INSTALL)))
clean :
	@rm  -rf $(REQUIRED_DIRS)

binclean :
	@rm -rf $(bindir)/*

.PHONY: all test clean doc runapp viewsvg oct octview sc max binclean mdoc
.SUFFIXES: .app .dsp .html .md
