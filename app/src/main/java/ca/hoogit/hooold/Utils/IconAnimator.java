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

    public static final long ANIMATION_DURATION = 200L;
    public static final int ANIMATION_FLIP = R.animator.flip;
    public static final int ANIMATION_FLIP_REVERSE = R.animator.flip_reverse;

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

    public boolean start() {
        if (!isOriginal) {
            flip(mIcon, ANIMATION_FLIP, true);
        } else {
            flip(mIconReverse, ANIMATION_FLIP, false);
        }
        return isOriginal = !isOriginal;
    }

    private void flip(ImageView view, int animation, final boolean forward) {
        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(mContext, animation);
        anim.setTarget(view);
        anim.setDuration(ANIMATION_DURATION);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!animationFinished) {
                    if (forward) {
                        forward();
                    } else {
                        backward();
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

    private void forward() {
        if (!isReversed) {
            flip(mIconReverse, ANIMATION_FLIP_REVERSE, true);
            mIconReverse.setVisibility(View.VISIBLE);
        } else {
            flip(mIcon, ANIMATION_FLIP, true);
            mIconReverse.setVisibility(View.INVISIBLE);
        }
    }

    private void backward() {
        if (isReversed) {
            flip(mIcon, ANIMATION_FLIP_REVERSE, false);
            mIconReverse.setVisibility(View.VISIBLE);
        } else {
            flip(mIconReverse, ANIMATION_FLIP, false);
            mIconReverse.setVisibility(View.INVISIBLE);
        }
    }

    public interface OnAnimationComplete {
        void animationCompleted(boolean reversed);
    }
}
