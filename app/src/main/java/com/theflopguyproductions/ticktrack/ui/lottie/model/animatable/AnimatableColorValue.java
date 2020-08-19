package com.theflopguyproductions.ticktrack.ui.lottie.model.animatable;

import com.theflopguyproductions.ticktrack.ui.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.theflopguyproductions.ticktrack.ui.lottie.animation.keyframe.ColorKeyframeAnimation;
import com.theflopguyproductions.ticktrack.ui.lottie.value.Keyframe;

import java.util.List;

public class AnimatableColorValue extends BaseAnimatableValue<Integer, Integer> {
  public AnimatableColorValue(List<Keyframe<Integer>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<Integer, Integer> createAnimation() {
    return new ColorKeyframeAnimation(keyframes);
  }
}
