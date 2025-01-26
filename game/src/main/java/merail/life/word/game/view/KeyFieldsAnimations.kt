package merail.life.word.game.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import merail.life.word.game.COLUMNS_COUNT

private const val BOUNCE_ANIMATION_TOP_VALUE = -10f
private const val BOUNCE_ANIMATION_BOTTOM_VALUE = 0f
private const val BOUNCE_ANIMATION_DURATION = 80

private const val VIBRATE_ANIMATION_START_VALUE = -10f
private const val VIBRATE_ANIMATION_END_VALUE = 10f
private const val VIBRATE_ANIMATION_DURATION = 40

private const val ERROR_COLOR_ANIMATION_DURATION = 10
private const val ERROR_COLOR_ANIMATION_DELAY = 500

private const val FLIP_ANIMATION_DELAY = 300L
private const val FLIP_ANIMATION_TARGET_VALUE = 180f
private const val FLIP_ANIMATION_DURATION = 600

internal fun CoroutineScope.launchBounceAnimation(
    bounceAnimation: Animatable<Float, AnimationVector1D>,
) {
    launch {
        bounceAnimation.animateTo(
            targetValue = BOUNCE_ANIMATION_TOP_VALUE,
            animationSpec = tween(
                durationMillis = BOUNCE_ANIMATION_DURATION,
            )
        )
        bounceAnimation.animateTo(
            targetValue = BOUNCE_ANIMATION_BOTTOM_VALUE,
            animationSpec = tween(
                durationMillis = BOUNCE_ANIMATION_DURATION,
            )
        )
    }
}

internal fun CoroutineScope.launchVibrateAnimation(
    vibrateAnimation: Animatable<Float, AnimationVector1D>,
) {
    launch {
        repeat(4) {
            vibrateAnimation.animateTo(
                targetValue = VIBRATE_ANIMATION_END_VALUE,
                animationSpec = tween(
                    durationMillis = VIBRATE_ANIMATION_DURATION,
                )
            )
            vibrateAnimation.animateTo(
                targetValue = VIBRATE_ANIMATION_START_VALUE,
                animationSpec = tween(
                    durationMillis = VIBRATE_ANIMATION_DURATION,
                )
            )
        }
        vibrateAnimation.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = VIBRATE_ANIMATION_DURATION,
            )
        )
    }
}

internal fun CoroutineScope.launchErrorColorAnimation(
    animatableColor: Animatable<Color, AnimationVector4D>,
    initialColor: Color,
    targetColor: Color,
) {
    launch {
        animatableColor.animateTo(
            targetValue = targetColor,
            animationSpec = tween(
                durationMillis = ERROR_COLOR_ANIMATION_DURATION,
            ),
        )
        animatableColor.animateTo(
            targetValue = initialColor,
            animationSpec = tween(
                delayMillis = ERROR_COLOR_ANIMATION_DELAY,
                durationMillis = ERROR_COLOR_ANIMATION_DURATION,
            ),
        )
    }
}

internal fun CoroutineScope.launchFlipAnimation(
    column: Int,
    rotationAnimation: Animatable<Float, AnimationVector1D>,
    isLastFilledRow: Boolean,
    onAnimationEnd: () -> Unit,
) {
    launch {
        delay(column * FLIP_ANIMATION_DELAY)
        rotationAnimation.animateTo(
            targetValue = FLIP_ANIMATION_TARGET_VALUE,
            animationSpec = tween(
                durationMillis = FLIP_ANIMATION_DURATION,
            )
        )
        if (isLastFilledRow && column == COLUMNS_COUNT - 1) {
            onAnimationEnd()
        }
    }
}
