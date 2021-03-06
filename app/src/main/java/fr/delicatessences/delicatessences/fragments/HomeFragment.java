package fr.delicatessences.delicatessences.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;

import net.mediavrog.irr.IrrLayout;

import java.sql.SQLException;
import java.util.Random;

import fr.delicatessences.delicatessences.R;
import fr.delicatessences.delicatessences.activities.MainActivity;
import fr.delicatessences.delicatessences.activities.OrmLiteBaseActionBarActivity;
import fr.delicatessences.delicatessences.activities.SearchableActivity;
import fr.delicatessences.delicatessences.adapters.LastRecipesCursorAdapter;
import fr.delicatessences.delicatessences.decorators.SimpleDividerItemDecoration;
import fr.delicatessences.delicatessences.loaders.CustomAsyncTaskLoader;
import fr.delicatessences.delicatessences.loaders.LastRecipeCursorLoader;
import fr.delicatessences.delicatessences.model.DatabaseHelper;
import fr.delicatessences.delicatessences.model.EssentialOil;
import fr.delicatessences.delicatessences.utils.CustomOnUserActionListener;
import fr.delicatessences.delicatessences.utils.ImageUtils;

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object>{

    private static final String HOME_DISPLAY_COUNT_PREF = "homeDisplayCount";
    private final static int HOME_DISPLAY_COUNT = 2;
    private static final int[] TUTO_IMAGES_ID = new int[]{R.drawable.pic_add_recipe, R.drawable.pic_add_bottle,
            R.drawable.pic_oils, R.drawable.pic_way};
    private static final int[] TUTO_IMAGES_LOW_ID = new int[]{R.drawable.pic_add_recipe_low, R.drawable.pic_add_bottle_low,
            R.drawable.pic_oils_low, R.drawable.pic_way_low};

    private static final Logger logger = LoggerFactory.getLogger(HomeFragment.class);

    private OrmLiteBaseActionBarActivity mActivity;
    private TextView mOilCardTitle;
    private TextView mOilContentTitle;
    private ViewGroup mOilCard;
    private ImageView mOilCardImage;
    private ViewGroup mWelcomeCard;
    private ImageView mWelcomeCardImage;
    private LastRecipesCursorAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ViewGroup mLastRecipeCard;
    private ImageView mLastRecipeCardImage;
    private Button mOilCardButton;
    private ShowcaseView mShowcaseView;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home,
                container, false);

        Resources resources = getResources();

        mWelcomeCard = (ViewGroup) view.findViewById(R.id.card_view_welcome);
        mWelcomeCardImage = (ImageView) view.findViewById(R.id.welcome_card_image);

        mOilCardTitle = (TextView) view.findViewById(R.id.oil_card_title);
        mOilContentTitle = (TextView) view.findViewById(R.id.oil_card_content);
        mOilCard = (ViewGroup) view.findViewById(R.id.card_view_oil);
        mOilCardImage = (ImageView) view.findViewById(R.id.oil_card_image);
        mOilCardButton = (Button) view.findViewById(R.id.oil_card_button);

        mLastRecipeCard = (ViewGroup) view.findViewById(R.id.card_view_recipe);
        mLastRecipeCardImage = (ImageView) view.findViewById(R.id.recipes_card_image);
        mAdapter = new LastRecipesCursorAdapter(mActivity);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recipes_card_content);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getBaseContext());

        boolean showWelcome = preferences.getBoolean("showWelcome", true);

        if (showWelcome) {
            mWelcomeCard = (ViewGroup) view.findViewById(R.id.card_view_welcome);
            mWelcomeCardImage = (ImageView) view.findViewById(R.id.welcome_card_image);
            ImageUtils.loadDrawable(getActivity(), R.drawable.pic_welcome, mWelcomeCardImage);
            mWelcomeCard.setVisibility(View.VISIBLE);
        }

        Random ran = new Random();
        boolean showAbout = ran.nextBoolean();
        if (showAbout){
            ViewGroup card = (ViewGroup) view.findViewById(R.id.card_view_about);
            ImageView imageView = (ImageView) view.findViewById(R.id.about_card_image);
            ImageUtils.loadDrawable(getActivity(), R.drawable.pic_about, imageView);
            card.setVisibility(View.VISIBLE);
        }else{
            ViewGroup card = (ViewGroup) view.findViewById(R.id.card_view_helpus);
            ImageView imageView = (ImageView) view.findViewById(R.id.helpus_card_image);
            ImageUtils.loadDrawable(getActivity(), R.drawable.pic_help, imageView);
            card.setVisibility(View.VISIBLE);
        }

        String[] tutorialTitles = resources.getStringArray(R.array.tutorial_titles);
        String[] tutorialContents = resources.getStringArray(R.array.tutorial_contents);
        int randomTuto = ran.nextInt(tutorialContents.length);
        TextView tutoTitle = (TextView) view.findViewById(R.id.tuto_card_title);
        tutoTitle.setText(tutorialTitles[randomTuto]);
        TextView tutoContent = (TextView) view.findViewById(R.id.tuto_card_content);
        tutoContent.setText(tutorialContents[randomTuto]);
        ImageView tutoImageView = (ImageView) view.findViewById(R.id.tuto_card_image);
        int tutoLowDrawableId = TUTO_IMAGES_LOW_ID[randomTuto];
        tutoImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, tutoLowDrawableId));
        int tutoDrawableId = TUTO_IMAGES_ID[randomTuto];
        ImageUtils.loadDrawable(getActivity(), tutoDrawableId, tutoImageView);


        IrrLayout irrLayout = (IrrLayout) view.findViewById(R.id.irr_layout);
        if (irrLayout != null){
            irrLayout.setOnUserActionListener(new CustomOnUserActionListener());
        }


        return view;
	}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);


        Resources resources = getResources();
        final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        Target target = new Target(){

            @Override
            public Point getPoint() {
                View view = toolbar.findViewById(R.id.action_search);
                if (view != null){
                    return new ViewTarget(view).getPoint();
                }

                return null;
            }
        };


        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        int displayCount = preferences.getInt(HOME_DISPLAY_COUNT_PREF, 1);

        if (displayCount == HOME_DISPLAY_COUNT) {
            mShowcaseView = new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setTarget(target)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setContentTitle(resources.getString(R.string.home_showcase_title))
                    .setContentText(resources.getString(R.string.home_showcase_content))
                    .replaceEndButton(R.layout.view_custom_button)
                    .build();
            mShowcaseView.setButtonPosition(lps);
        }
        SharedPreferences.Editor e = preferences.edit();
        e.putInt(HOME_DISPLAY_COUNT_PREF, displayCount + 1);
        e.apply();

    }


    public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();

	   Bundle args = new Bundle();
	    args.putInt(MainActivity.EXTRA_VIEW_TYPE, ViewType.HOME.ordinal());
	   fragment.setArguments(args);

	    return fragment;
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_home_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowcaseView != null && mShowcaseView.isShown()){
                    mShowcaseView.hide();
                }
            }
        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(getContext(), SearchableActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        MainActivity activity = (MainActivity) getActivity();
        activity.setDrawerIndicatorEnabled(true);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayShowTitleEnabled(true);
            String[] titles = getResources().getStringArray(R.array.drawer_items);
            actionBar.setTitle(titles[ViewType.HOME.ordinal()]);
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof MainActivity) {
            mActivity = (MainActivity) getActivity();
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MainActivity");
        }

        setHasOptionsMenu(true);
    }


    @SuppressWarnings("unchecked")
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        switch (id){
            case 0:
                return new CustomAsyncTaskLoader<Object>(mActivity) {
                    @Override
                    public Object loadInBackground() {
                       EssentialOil essentialOil = null;

                        try {
                            DatabaseHelper helper = (DatabaseHelper) mActivity.getHelper();
                            Dao<EssentialOil, Integer> dao = helper.getEssentialOilDao();
                            QueryBuilder<EssentialOil, Integer> queryBuilder = dao.queryBuilder();
                            queryBuilder.orderByRaw("RANDOM()");
                            essentialOil = queryBuilder.queryForFirst();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e){
                            logger.trace(e.getMessage());
                        }

                        return essentialOil;
                    }
                };


//            case 1:
//                return new CustomAsyncTaskLoader<Object>(mActivity) {
//                    @Override
//                    public Object loadInBackground() {
//                        Configuration configuration = null;
//
//                        try {
//                            DatabaseHelper helper = (DatabaseHelper) mActivity.getHelper();
//                            Dao<Configuration, Integer> dao = helper.getConfigurationDao();
//                            QueryBuilder<Configuration, Integer> queryBuilder = dao.queryBuilder();
//                            configuration = queryBuilder.queryForFirst();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//
//                        return configuration;
//                    }
//                };

            case 2:
                return new CustomAsyncTaskLoader<Object>(mActivity) {
                    @Override
                    public Object loadInBackground() {

                        try {
                            DatabaseHelper helper = (DatabaseHelper) mActivity.getHelper();
                            helper.loadCategories();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e){
                            logger.trace(e.getMessage());
                        }

                        return null;
                    }
                };

            case 3:
                return new LastRecipeCursorLoader(mActivity);

            default:
                return null;

        }
    }



    @Override
    public void onLoadFinished(Loader<Object> loader, Object o) {
        switch (loader.getId()) {
            case 0:
                if (o instanceof EssentialOil) {
                    EssentialOil essentialOil = (EssentialOil) o;
                    Resources resources = getResources();
                    String title = resources.getString(R.string.zoom_on) + " " + essentialOil.getName();
                    mOilCardTitle.setText(title);
                    mOilContentTitle.setText(essentialOil.getDescription());
                    mOilCardButton.setTag(essentialOil.getId());
                    int lowResId = resources.getIdentifier("pic_" + essentialOil.getImage() + "_low", "drawable", mActivity.getPackageName());
                    mOilCardImage.setImageDrawable(ContextCompat.getDrawable(mActivity, lowResId));
                    int resId = resources.getIdentifier("pic_" + essentialOil.getImage(), "drawable", mActivity.getPackageName());
                    ImageUtils.loadDrawable(getActivity(), resId, mOilCardImage);
                    mOilCard.setVisibility(View.VISIBLE);
                }else{
                    mOilCard.setVisibility(View.GONE);
                }
                break;

//            case 1:
//                if (o instanceof Configuration){
//                    Configuration configuration = (Configuration) o;
//                    if (configuration.isShowWelcomeMessage()){
//                        ImageUtils.loadDrawable(getActivity(), R.drawable.pic_welcome, mWelcomeCardImage);
//                        mWelcomeCard.setVisibility(View.VISIBLE);
//                    }
//                }
//
//                break;

            case 3:
                if (o instanceof Cursor){
                    Cursor data = (Cursor) o;
                    int nbRecipes = data.getCount();
                    if (nbRecipes > 0) {
                        mAdapter.changeCursor(data);
                        ImageUtils.loadDrawable(getActivity(), R.drawable.pic_recipes, mLastRecipeCardImage);
                        ViewGroup.LayoutParams layoutParams = mRecyclerView.getLayoutParams();
                        layoutParams.height = nbRecipes * getResources().getDimensionPixelSize(R.dimen.card_view_list_item_height);
                        mRecyclerView.setLayoutParams(layoutParams);
                        mLastRecipeCard.setVisibility(View.VISIBLE);
                    }
                }
                break;

            default:break;


        }

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        switch (loader.getId()){
            case 3:
                mAdapter.changeCursor(null);
                mLastRecipeCard.setVisibility(View.GONE);
                break;
        }
    }



    @Override
    public void onStart() {
        getLoaderManager().restartLoader(0, null, this);
        //getLoaderManager().restartLoader(1, null, this);
        getLoaderManager().restartLoader(2, null, this);
        getLoaderManager().restartLoader(3, null, this);
        super.onStart();
    }



    public void hideWelcomeCard(){
        mWelcomeCard.setVisibility(View.GONE);
    }


    public int getEssentialOil(){
        if (mOilCardButton != null){
            Object tag = mOilCardButton.getTag();
            if (tag instanceof Integer){
                return (Integer) tag;
            }
        }

        return -1;
    }

}
