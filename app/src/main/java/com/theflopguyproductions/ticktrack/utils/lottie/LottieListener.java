package com.theflopguyproductions.ticktrack.ui.lottie;

/**
 * Receive a result with either the value or exception for a {@link LottieTask}
 */
public interface LottieListener<T> {
  void onResult(T result);
}
