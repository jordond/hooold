/**
 * Copyright (C) 2015, Jordon de Hoog
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package ca.hoogit.hooold.Utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import ca.hoogit.hooold.R;

/**
 * @author jordon
 *
 * Date    14/08/15
 * Description
 *
 */
public class IconAnimator {

    public static final int ANIMATION_FLIP = R.animator.flip;
    public static final int ANIMATION_FLIP_REVERSE = R.animator.flip_reverse;

    public static final long ANIMATION_DURATION = 200L;

    private Context mContext;
    private ImageView mIcon;
    private ImageView mIconReverse;
    private boolean isReversed;
    private boolean animationFinished;
    private boolean isOriginal;
    private OnAnimationComplete mListener;

    public IconAnimator(Context context, ImageView icon, ImageView iconReverse) {
        this.mContext = context;
        this.mIcon = icon;
        this.mIconReverse = iconReverse;
    }

    public void setListener(OnAnimationComplete listener) {
        mListener = listener;
    }

    public boolean start(long duration) {
        if (!isOriginal) {
            flip(mIcon, ANIMATION_FLIP, true, duration);
        } else {
            flip(mIconReverse, ANIMATION_FLIP, false, duration);
        }
        return isOriginal = !isOriginal;
    }

    public void start(boolean selected) {
        if (!selected) {
            flip(mIcon, ANIMATION_FLIP, true, ANIMATION_DURATION);
        } else{
            flip(mIconReverse, ANIMATION_FLIP, false, ANIMATION_DURATION);
        }
    }

    public boolean start() {
        return start(ANIMATION_DURATION);
    }

    public void reset(boolean shouldReset) {
        if (shouldReset) {
            flip(mIcon, ANIMATION_FLIP, false, ANIMATION_DURATION);
        }
    }

    private void flip(ImageView view, int animation, final boolean forward, final long duration) {
        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(mContext, animation);
        anim.setTarget(view);
        anim.setDuration(duration);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!animationFinished) {
                    if (forward) {
                        flip(mIconReverse, ANIMATION_FLIP_REVERSE, true, duration);
                    } else {
                        flip(mIcon, ANIMATION_FLIP_REVERSE, false, duration);
                    }
                    if (mIconReverse.getVisibility() == View.VISIBLE) {
                        mIconReverse.setVisibility(View.INVISIBLE);
                    } else {
                        mIconReverse.setVisibility(View.VISIBLE);
                    }
                    isReversed = !isReversed;
                    animationFinished = true;
                } else {
                    animationFinished = false;
                    if (mListener != null) {
                        mListener.animationCompleted(isOriginal);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    public interface OnAnimationComplete {
        void animationCompleted(boolean reversed);
    }
}
