package com.vacuumlabs.performancecheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AnimationsViewModel : ViewModel() {

    // flows for randomized moving positions
    val movingLogoPositionFlows = mutableListOf<MutableStateFlow<Pair<Float, Float>>>()

    // flows for randomized initial positions of disappearing logos
    val disappearingLogoPositionsFlows = mutableListOf<MutableStateFlow<Pair<Float, Float>>>()

    // flows that mark whether logo should become visible or hidden
    val disappearingLogoVisibilityFlows = mutableListOf<MutableStateFlow<Boolean>>()

    // flows for randomized initial positions of scaling logos
    val scalingLogoPositionsFlows = mutableListOf<MutableStateFlow<Pair<Float, Float>>>()

    // flows that mark whether logo should scale up or down
    val scalingLogoTriggeredFlows = mutableListOf<MutableStateFlow<Boolean>>()

    init {
        // for each disappearing element, we prepare random location on screen and initial
        // state (false as not present/visible)
        for (i in 1..disappearingElementsCount) {
            disappearingLogoPositionsFlows.add(MutableStateFlow(rand() to rand()))
            disappearingLogoVisibilityFlows.add(MutableStateFlow(false))
        }
        // for each scaling element, we prepare random location on screen and initial
        // state (false as not scaled)
        for (i in 1..scalingElementsCount) {
            scalingLogoPositionsFlows.add(MutableStateFlow(rand() to rand()))
            scalingLogoTriggeredFlows.add(MutableStateFlow(false))
        }
        // for each moving element, we prepare initial random location on screen
        for (i in 1..movingElementsCount) {
            movingLogoPositionFlows.add(MutableStateFlow(rand() to rand()))
        }
        cycleMoving()
        cycleDisappearing()
        cycleScaling()
    }

    /**
     * Functions that is calling itself in cycle and triggers moving animations.
     */
    private fun cycleMoving(counter: Long = 0L) {
        viewModelScope.launch {
            delay(cycleDuration / movingElementsCount)
            randomizeMovingPositionAt((counter % movingElementsCount).toInt())
            cycleMoving((counter + 1) % movingElementsCount)
        }
    }

    /**
     * Functions that is calling itself in cycle and triggers disappearing animations.
     */
    private fun cycleDisappearing(counter: Long = 0L, revert: Boolean = false) {
        viewModelScope.launch {
            delay(cycleDuration / movingElementsCount)
            disappearingLogoVisibilityFlows[(counter % disappearingElementsCount).toInt()].value =
                revert
            cycleDisappearing(
                counter = (counter + 1) % disappearingElementsCount,
                revert = if (((counter + 1) % disappearingElementsCount) == 0L) !revert else revert
            )
        }
    }

    /**
     * Functions that is calling itself in cycle and triggers scaling animations.
     */
    private fun cycleScaling(counter: Long = 0L, revert: Boolean = false) {
        viewModelScope.launch {
            delay(cycleDuration / scalingElementsCount)
            scalingLogoTriggeredFlows[(counter % scalingElementsCount).toInt()].value = revert
            cycleScaling(
                counter = (counter + 1) % scalingElementsCount,
                revert = if (((counter + 1) % scalingElementsCount) == 0L) !revert else revert
            )
        }
    }

    /**
     * Sets random point on screen, where image is supposed to be present. It is pair
     * of values from interval <0, 1> , where "first" marks X axis position as fraction of width
     * and "second" marks Y axis position as fraction of height.
     */
    private fun randomizeMovingPositionAt(index: Int) {
        this.movingLogoPositionFlows[index].value = rand() to rand()
    }


    private fun rand(): Float {
        val start = 0f
        val end = 1f
        require(start <= end) { "Illegal Argument" }
        val random = Math.random().toFloat()
        return (random - random.toInt()) * (end - start) + start
    }

    companion object {
        const val cycleDuration = 1000L

        const val disappearingElementsCount = 10
        const val scalingElementsCount = 10
        const val movingElementsCount = 10
    }
}