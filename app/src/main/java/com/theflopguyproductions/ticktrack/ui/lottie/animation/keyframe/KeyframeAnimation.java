package com.theflopguyproductions.ticktrack.ui.lottie.animation.keyframe;

import com.theflopguyproductions.ticktrack.ui.lottie.value.Keyframe;

import java.util.List;

abstract class KeyframeAnimation<T> extends BaseKeyframeAnimation<T, T> {
  KeyframeAnimation(List<? extends Keyframe<T>> keyframes) {
    super(keyframes);
  }
}
