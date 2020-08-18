package com.theflopguyproductions.ticktrack.ui.lottie.utils;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.os.Build;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class BaseLottieAnimator extends ValueAnimator {
  private final Set<AnimatorUpdateListener> updateListeners = new CopyOnWriteArraySet<>();
  private final Set<AnimatorListener> listeners = new CopyOnWriteArraySet<>();

  @Override public long getStartDelay() {
    throw new UnsupportedOperationException("LottieAnimator does not support getStartDelay.");
  }

  @Override public void setStartDelay(long startDelay) {
    throw new UnsupportedOperationException("LottieAnimator does not support setStartDelay.");
  }
  @Override public ValueAnimator setDuration(long duration) {
    throw new UnsupportedOperationException("LottieAnimator does not support setDuration.");
  }

  @Override public void setInterpolator(TimeInterpolator value) {
    throw new UnsupportedOperationException("LottieAnimator does not support setInterpolator.");
  }

  public void addUpdateListener(AnimatorUpdateListener listener) {
    updateListeners.add(listener);
  }

  public void removeUpdateListener(AnimatorUpdateListener listener) {
    updateListeners.remove(listener);
  }

  public void removeAllUpdateListeners() {
    updateListeners.clear();
  }

  public void addListener(AnimatorListener listener) {
    listeners.add(listener);
  }

  public void removeListener(AnimatorListener listener) {
    listeners.remove(listener);
  }

  public void removeAllListeners() {
    listeners.clear();
  }

  void notifyStart(boolean isReverse) {
    for (AnimatorListener listener : listeners) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        listener.onAnimationStart(this, isReverse);
      } else {
        listener.onAnimationStart(this);
      }
    }
  }

  void notifyRepeat() {
    for (AnimatorListener listener : listeners) {
      listener.onAnimationRepeat(this);
    }
  }

  void notifyEnd(boolean isReverse) {
    for (AnimatorListener listener : listeners) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        listener.onAnimationEnd(this, isReverse);
      } else {
        listener.onAnimationEnd(this);
      }
    }
  }

  void notifyCancel() {
    for (AnimatorListener listener : listeners) {
      listener.onAnimationCancel(this);
    }
  }

  void notifyUpdate() {
    for (AnimatorUpdateListener listener : updateListeners) {
      listener.onAnimationUpdate(this);
    }
  }
}
