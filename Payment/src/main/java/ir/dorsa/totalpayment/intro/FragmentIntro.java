package ir.dorsa.totalpayment.intro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ir.dorsa.totalpayment.R;
import ir.dorsa.totalpayment.customView.ViewPagerIndicator;
import ir.dorsa.totalpayment.payment.Payment;

public class FragmentIntro extends Fragment {

    private static String KEY_RESOURCE_LAYOUT = "";
    private View pView;
    private ViewPager viewPager;
    private RelativeLayout relEnter;
    private ViewPagerIndicator viewPagerIndicator;

    private View back;

    private View prev;
    private View next;

    private int layoutId[] = new int[4];
    private interaction mListener;

    public static FragmentIntro newInstance(int[] splashResource) {
        FragmentIntro fragmentIntro = new FragmentIntro();
        Bundle args = new Bundle();
        args.putIntArray(KEY_RESOURCE_LAYOUT, splashResource);
        fragmentIntro.setArguments(args);
        return fragmentIntro;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            layoutId = getArguments().getIntArray(KEY_RESOURCE_LAYOUT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.payment_fragment_intro, container, false);

        viewPager = pView.findViewById(R.id.view_pager);
        relEnter = pView.findViewById(R.id.enter);
        viewPagerIndicator = pView.findViewById(R.id.view_pager_indicator);
        back = pView.findViewById(R.id.back);

        prev = pView.findViewById(R.id.prev);
        next = pView.findViewById(R.id.next);


        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onBackPressed();
                    }
                }
            });
        }

        if (prev != null) {
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewPager.getCurrentItem() > 0) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                    }
                }
            });
        }

        if (next != null) {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewPager.getCurrentItem() < (layoutId.length - 1)) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                }
            });
        }


        setPrevVisibility(false);
        setNextVisibility(layoutId.length > 1);


        setupViewPager();

        viewPagerIndicator.setupWithViewPager(viewPager);
        viewPagerIndicator.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setNextVisibility(true);
                setPrevVisibility(true);

                if (position == 0) {
                    setPrevVisibility(false);
                }

                if (position == layoutId.length - 1) {

                    setNextVisibility(false);

                    relEnter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mListener != null) {
                                mListener.onEnterSelected();
                            }
                        }
                    });
                    showEnter();
                } else if (relEnter.getVisibility() == View.VISIBLE) {
                    relEnter.setOnClickListener(null);
                    hideenter();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (layoutId.length <= 1) {
            relEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onEnterSelected();
                    }
                }
            });
            showEnter();
        }

        return pView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof interaction) {
            mListener = (interaction) context;
        }
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        for (int layoutId : layoutId) {
            adapter.addFragment(new FragmentPage().newInstance(layoutId));
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        if (layoutId.length == 1) {
            relEnter.setVisibility(View.VISIBLE);
            relEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onEnterSelected();
                    }
                }
            });
        }
    }


    private void showEnter() {
        relEnter.setPivotX(relEnter.getMeasuredWidth() / 2);
        relEnter.setPivotY(relEnter.getMeasuredHeight() / 2);

        relEnter.setScaleX(0.0f);
        relEnter.setScaleY(0.0f);

        ObjectAnimator animScaleX = ObjectAnimator.ofFloat(relEnter, "scaleX", 0f, 1.2f, 0.9f, 1.1f, 1.0f);
        ObjectAnimator animScaleY = ObjectAnimator.ofFloat(relEnter, "scaleY", 0f, 1.2f, 0.9f, 1.1f, 1.0f);
        animScaleY.setDuration(300);
        animScaleX.setDuration(300);

        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(animScaleX).with(animScaleY);
        scaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                relEnter.setVisibility(View.VISIBLE);
            }
        });
        scaleDown.start();
    }

    private void hideenter() {
        relEnter.setPivotX(relEnter.getMeasuredWidth() / 2);
        relEnter.setPivotY(relEnter.getMeasuredHeight() / 2);

        relEnter.setScaleX(0.0f);
        relEnter.setScaleY(0.0f);

        ObjectAnimator animScaleX = ObjectAnimator.ofFloat(relEnter, "scaleX", 1.0f, 1.1f, 0.0f);
        ObjectAnimator animScaleY = ObjectAnimator.ofFloat(relEnter, "scaleY", 1.0f, 1.1f, 0.0f);
        animScaleY.setDuration(300);
        animScaleX.setDuration(300);

        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(animScaleX).with(animScaleY);
        scaleDown.start();
        scaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                relEnter.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setNextVisibility(boolean visibility) {
        if (next == null) {
            return;
        }

        if (Payment.marketId != null && !Payment.marketId.isEmpty()) {
            if (visibility) {
                next.setVisibility(View.VISIBLE);
            } else {
                next.setVisibility(View.GONE);
            }
        } else {
            next.setVisibility(View.GONE);
        }


    }

    private void setPrevVisibility(boolean visibility) {
        if (prev == null) {
            return;
        }
        if (Payment.marketId != null && !Payment.marketId.isEmpty()) {
            if (visibility) {
                prev.setVisibility(View.VISIBLE);
            } else {
                prev.setVisibility(View.GONE);
            }
        } else {
            prev.setVisibility(View.GONE);
        }
    }


    public interface interaction {
        void onEnterSelected();

        void onBackPressed();
    }
}
