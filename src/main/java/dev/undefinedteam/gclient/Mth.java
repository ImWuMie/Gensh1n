package dev.undefinedteam.gclient;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import org.apache.commons.lang3.math.NumberUtils;

public class Mth {
   public static float sqrt(float value) {
      return (float)Math.sqrt(value);
   }

   public static int floor(float value) {
      int i = (int)value;
      return value < (float)i ? i - 1 : i;
   }

   public static int floor(double value) {
      int i = (int)value;
      return value < (double)i ? i - 1 : i;
   }

   public static long lfloor(double value) {
      long l = (long)value;
      return value < (double)l ? l - 1L : l;
   }

   public static float abs(float value) {
      return Math.abs(value);
   }

   public static int abs(int value) {
      return Math.abs(value);
   }

   public static int ceil(float value) {
      int i = (int)value;
      return value > (float)i ? i + 1 : i;
   }

   public static int ceil(double value) {
      int i = (int)value;
      return value > (double)i ? i + 1 : i;
   }

   public static int clamp(int value, int min, int max) {
      return Math.min(Math.max(value, min), max);
   }

   public static float clamp(float value, float min, float max) {
      return value < min ? min : Math.min(value, max);
   }

   public static double clamp(double value, double min, double max) {
      return value < min ? min : Math.min(value, max);
   }

   public static double clampedLerp(double start, double end, double delta) {
      if (delta < 0.0D) {
         return start;
      } else {
         return delta > 1.0D ? end : lerp(delta, start, end);
      }
   }

   public static float clampedLerp(float start, float end, float delta) {
      if (delta < 0.0F) {
         return start;
      } else {
         return delta > 1.0F ? end : lerp(delta, start, end);
      }
   }

   public static double absMax(double a, double b) {
      if (a < 0.0D) {
         a = -a;
      }

      if (b < 0.0D) {
         b = -b;
      }

      return Math.max(a, b);
   }

   public static int floorDiv(int dividend, int divisor) {
      return Math.floorDiv(dividend, divisor);
   }

   public static int nextInt(Random random, int min, int max) {
      return min >= max ? min : random.nextInt(max - min + 1) + min;
   }

   public static float nextFloat(Random random, float min, float max) {
      return min >= max ? min : random.nextFloat() * (max - min) + min;
   }

   public static double nextDouble(Random random, double min, double max) {
      return min >= max ? min : random.nextDouble() * (max - min) + min;
   }

   public static boolean approximatelyEquals(float a, float b) {
      return Math.abs(b - a) < 1.0E-5F;
   }

   public static boolean approximatelyEquals(double a, double b) {
      return Math.abs(b - a) < 9.999999747378752E-6D;
   }

   public static int floorMod(int dividend, int divisor) {
      return Math.floorMod(dividend, divisor);
   }

   public static float floorMod(float dividend, float divisor) {
      return (dividend % divisor + divisor) % divisor;
   }

   public static double floorMod(double dividend, double divisor) {
      return (dividend % divisor + divisor) % divisor;
   }

   public static boolean isMultipleOf(int a, int b) {
      return a % b == 0;
   }

   public static int wrapDegrees(int degrees) {
      int i = degrees % 360;
      if (i >= 180) {
         i -= 360;
      }

      if (i < -180) {
         i += 360;
      }

      return i;
   }

   public static float wrapDegrees(float degrees) {
      float f = degrees % 360.0F;
      if (f >= 180.0F) {
         f -= 360.0F;
      }

      if (f < -180.0F) {
         f += 360.0F;
      }

      return f;
   }

   public static double wrapDegrees(double degrees) {
      double d = degrees % 360.0D;
      if (d >= 180.0D) {
         d -= 360.0D;
      }

      if (d < -180.0D) {
         d += 360.0D;
      }

      return d;
   }

   public static float subtractAngles(float start, float end) {
      return wrapDegrees(end - start);
   }

   public static float angleBetween(float first, float second) {
      return abs(subtractAngles(first, second));
   }

   public static float clampAngle(float value, float mean, float delta) {
      float f = subtractAngles(value, mean);
      float g = clamp(f, -delta, delta);
      return mean - g;
   }

   public static float stepTowards(float from, float to, float step) {
      step = abs(step);
      return from < to ? clamp(from + step, from, to) : clamp(from - step, to, from);
   }

   public static float stepUnwrappedAngleTowards(float from, float to, float step) {
      float f = subtractAngles(from, to);
      return stepTowards(from, from + f, step);
   }

   public static int parseInt(String string, int fallback) {
      return NumberUtils.toInt(string, fallback);
   }

   public static int smallestEncompassingPowerOfTwo(int value) {
      int i = value - 1;
      i |= i >> 1;
      i |= i >> 2;
      i |= i >> 4;
      i |= i >> 8;
      i |= i >> 16;
      return i + 1;
   }

   public static boolean isPowerOfTwo(int value) {
      return value != 0 && (value & value - 1) == 0;
   }

   public static int lerp(float delta, int start, int end) {
      return start + floor(delta * (float)(end - start));
   }

   public static float lerp(float delta, float start, float end) {
      return start + delta * (end - start);
   }

   public static double lerp(double delta, double start, double end) {
      return start + delta * (end - start);
   }

   public static double lerp2(double deltaX, double deltaY, double x0y0, double x1y0, double x0y1, double x1y1) {
      return lerp(deltaY, lerp(deltaX, x0y0, x1y0), lerp(deltaX, x0y1, x1y1));
   }

   public static double lerp3(double deltaX, double deltaY, double deltaZ, double x0y0z0, double x1y0z0, double x0y1z0, double x1y1z0, double x0y0z1, double x1y0z1, double x0y1z1, double x1y1z1) {
      return lerp(deltaZ, lerp2(deltaX, deltaY, x0y0z0, x1y0z0, x0y1z0, x1y1z0), lerp2(deltaX, deltaY, x0y0z1, x1y0z1, x0y1z1, x1y1z1));
   }

   public static float fractionalPart(float value) {
      return value - (float)floor(value);
   }

   public static double fractionalPart(double value) {
      return value - (double)lfloor(value);
   }

   public static float catmullRom(float delta, float p0, float p1, float p2, float p3) {
      return 0.5F * (2.0F * p1 + (p2 - p0) * delta + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * delta * delta + (3.0F * p1 - p0 - 3.0F * p2 + p3) * delta * delta * delta);
   }

   public static double perlinFade(double value) {
      return value * value * value * (value * (value * 6.0D - 15.0D) + 10.0D);
   }

   public static double perlinFadeDerivative(double value) {
      return 30.0D * value * value * (value - 1.0D) * (value - 1.0D);
   }

   public static int sign(double value) {
      if (value == 0.0D) {
         return 0;
      } else {
         return value > 0.0D ? 1 : -1;
      }
   }

   public static float lerpAngleDegrees(float delta, float start, float end) {
      return start + delta * wrapDegrees(end - start);
   }

   public static float wrap(float value, float maxDeviation) {
      return (Math.abs(value % maxDeviation - maxDeviation * 0.5F) - maxDeviation * 0.25F) / (maxDeviation * 0.25F);
   }

   public static float square(float n) {
      return n * n;
   }

   public static double square(double n) {
      return n * n;
   }

   public static int square(int n) {
      return n * n;
   }

   public static long square(long n) {
      return n * n;
   }

   public static double getLerpProgress(double value, double start, double end) {
      return (value - start) / (end - start);
   }

   public static float getLerpProgress(float value, float start, float end) {
      return (value - start) / (end - start);
   }

   public static double clampedMap(double value, double oldStart, double oldEnd, double newStart, double newEnd) {
      return clampedLerp(newStart, newEnd, getLerpProgress(value, oldStart, oldEnd));
   }

   public static float clampedMap(float value, float oldStart, float oldEnd, float newStart, float newEnd) {
      return clampedLerp(newStart, newEnd, getLerpProgress(value, oldStart, oldEnd));
   }

   public static double map(double value, double oldStart, double oldEnd, double newStart, double newEnd) {
      return lerp(getLerpProgress(value, oldStart, oldEnd), newStart, newEnd);
   }

   public static float map(float value, float oldStart, float oldEnd, float newStart, float newEnd) {
      return lerp(getLerpProgress(value, oldStart, oldEnd), newStart, newEnd);
   }

   public static int roundUpToMultiple(int value, int divisor) {
      return ceilDiv(value, divisor) * divisor;
   }

   public static int ceilDiv(int a, int b) {
      return -Math.floorDiv(-a, b);
   }

   public static int nextBetween(Random random, int min, int max) {
      return random.nextInt(max - min + 1) + min;
   }

   public static float nextBetween(Random random, float min, float max) {
      return random.nextFloat() * (max - min) + min;
   }

   public static float nextGaussian(Random random, float mean, float deviation) {
      return mean + (float)random.nextGaussian() * deviation;
   }

   public static double squaredHypot(double a, double b) {
      return a * a + b * b;
   }

   public static double hypot(double a, double b) {
      return Math.sqrt(squaredHypot(a, b));
   }

   public static double squaredMagnitude(double a, double b, double c) {
      return a * a + b * b + c * c;
   }

   public static double magnitude(double a, double b, double c) {
      return Math.sqrt(squaredMagnitude(a, b, c));
   }

   public static int roundDownToMultiple(double a, int b) {
      return floor(a / (double)b) * b;
   }
}
