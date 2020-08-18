package com.theflopguyproductions.ticktrack.ui.lottie.model.animatable;

import com.theflopguyproductions.ticktrack.ui.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.theflopguyproductions.ticktrack.ui.lottie.animation.keyframe.GradientColorKeyframeAnimation;
import com.theflopguyproductions.ticktrack.ui.lottie.model.content.GradientColor;
import com.theflopguyproductions.ticktrack.ui.lottie.value.Keyframe;

import java.util.List;

public class AnimatableGradientColorValue extends BaseAnimatableValue<GradientColor,
    GradientColor> {
  public AnimatableGradientColorValue(
      List<Keyframe<GradientColor>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<GradientColor, GradientColor> createAnimation() {
    return new GradientColorKeyframeAnimation(keyframes);
  }
}
