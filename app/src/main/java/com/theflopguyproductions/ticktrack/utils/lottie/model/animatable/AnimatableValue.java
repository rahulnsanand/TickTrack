package com.theflopguyproductions.ticktrack.ui.lottie.model.animatable;

import com.theflopguyproductions.ticktrack.ui.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.theflopguyproductions.ticktrack.ui.lottie.value.Keyframe;

import java.util.List;

public interface AnimatableValue<K, A> {
  List<Keyframe<K>> getKeyframes();
  boolean isStatic();
  BaseKeyframeAnimation<K, A> createAnimation();
}
