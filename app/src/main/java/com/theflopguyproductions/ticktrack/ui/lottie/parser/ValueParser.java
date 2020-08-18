package com.theflopguyproductions.ticktrack.ui.lottie.parser;


import com.theflopguyproductions.ticktrack.ui.lottie.parser.moshi.JsonReader;

import java.io.IOException;

interface ValueParser<V> {
  V parse(JsonReader reader, float scale) throws IOException;
}
