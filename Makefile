assets:
	fbx-conv-lin64 -f models/item.fbx
	cp models/item.g3db desktop/assets

desktop:
	sbt desktop/run
