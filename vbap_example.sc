
(
// Creata a 2D speaker array with 16 channels (lower ring in lilla salen)
~spkrArray = VBAPSpeakerArray.new(2, [-34.689614, -13.383763,  10.440725,  32.117788,  55.741675,  78.207673,  101.49442,  124.85167,  147.91193,  169.17789, -167.82013, -145.63454, -123.784, -102.64182, -79.887731, -57.926139]);
// Dump the speaker array to stdout
~spkrArray.speakers[1].dump;
// Load a new buffer with the spear array
~spkrBuffer = ~spkrArray.loadToBuffer;
)

(
~panVBAP = { |azi = 0, ele = 0, spr = 0|
	VBAP.ar(16, In.ar(33,1), ~spkrBuffer.bufnum, azi, ele, spr);
}.scope;
)

(
var aNumb, eNumb, sNumb, aSlid, eSlid, sSlid;
~guiWindowVBAP = Window.new.front;

aNumb = NumberBox(~guiWindowVBAP, Rect(20, 20, 70, 20));
aSlid = Slider(~guiWindowVBAP, Rect(20, 60, 20, 150)).action_({
	aNumb.value_(aSlid.value);
	~panVBAP.set(\azi, aSlid.value * 360);
});
aSlid.action.value;

eNumb = NumberBox(~guiWindowVBAP, Rect(90, 20, 70, 20));
eSlid = Slider(~guiWindowVBAP, Rect(90, 60, 20, 150)).action_({
	eNumb.value_(eSlid.value);
	~panVBAP.set(\ele, eSlid.value * 180 - 90);
});
eSlid.action.value;

sNumb = NumberBox(~guiWindowVBAP, Rect(160, 20, 70, 20));
sSlid = Slider(~guiWindowVBAP, Rect(160, 60, 20, 150)).action_({
	sNumb.value_(sSlid.value);
	~panVBAP.set(\spr, sSlid.value * 180);
});
sSlid.action.value;
)
)