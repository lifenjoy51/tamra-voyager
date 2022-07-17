package scene.world

import ViewModelProvider
import com.soywiz.klock.TimeSpan
import com.soywiz.korev.Key
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import ui.mainHeight
import ui.mainWidth

class WorldScene(viewModelProvider: ViewModelProvider) : Scene() {

    //private lateinit var bgSoundChannel: SoundChannel

    private val vm = viewModelProvider.worldViewModel

    private val worldView = WorldView(vm)

    override suspend fun sceneBeforeLeaving() {
        super.sceneBeforeLeaving()
        //bgSoundChannel.pause()
    }


    override suspend fun SContainer.sceneInit() {
        // world
        fixedSizeContainer(mainWidth, mainHeight) {
            worldView.draw(this)
        }

        // sky
        sprite(texture = resourcesVfs["sky.png"].readBitmap()) {
            scaledWidth = mainWidth.toDouble()
            scaledHeight = mainHeight / 5.0
        }

        // update
        addFixedUpdater(TimeSpan(100.0)) {
            onKeyInput()
            vm.move()
        }

        // play bg  sound
        //val bg = resourcesVfs["tamra.mp3"].readSound()
        //bgSoundChannel = bg.play(PlaybackTimes.INFINITE)
    }

    private fun onKeyInput() {
        when {
            views.input.keys[Key.RIGHT] -> vm.turnRight()
            views.input.keys[Key.LEFT] -> vm.turnLeft()

            views.input.keys[Key.DOWN] -> vm.toggleAnchor()

            views.input.keys[Key.Q] -> vm.sailLeft()
            views.input.keys[Key.E] -> vm.sailRight()

            views.input.keys[Key.W] -> vm.toggleSail()
        }
    }
}