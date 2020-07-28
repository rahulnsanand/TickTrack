package com.theflopguyproductions.ticktrack.ui.lottie.model.content;


import androidx.annotation.Nullable;

import com.theflopguyproductions.ticktrack.ui.lottie.LottieDrawable;
import com.theflopguyproductions.ticktrack.ui.lottie.animation.content.Content;
import com.theflopguyproductions.ticktrack.ui.lottie.model.layer.BaseLayer;

public interface ContentModel {
  @Nullable Content toContent(LottieDrawable drawable, BaseLayer layer);
}
