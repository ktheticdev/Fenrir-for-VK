package dev.ragnarok.fenrir.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.ragnarok.fenrir.Constants;
import dev.ragnarok.fenrir.Extra;
import dev.ragnarok.fenrir.R;
import dev.ragnarok.fenrir.adapter.LocalServerVideosAdapter;
import dev.ragnarok.fenrir.fragment.base.BaseMvpFragment;
import dev.ragnarok.fenrir.listener.EndlessRecyclerOnScrollListener;
import dev.ragnarok.fenrir.listener.PicassoPauseOnScrollListener;
import dev.ragnarok.fenrir.model.InternalVideoSize;
import dev.ragnarok.fenrir.model.Video;
import dev.ragnarok.fenrir.mvp.core.IPresenterFactory;
import dev.ragnarok.fenrir.mvp.presenter.VideosLocalServerPresenter;
import dev.ragnarok.fenrir.mvp.view.IVideosLocalServerView;
import dev.ragnarok.fenrir.place.PlaceFactory;
import dev.ragnarok.fenrir.util.AppPerms;
import dev.ragnarok.fenrir.util.CustomToast;
import dev.ragnarok.fenrir.util.ViewUtils;
import dev.ragnarok.fenrir.view.MySearchView;

import static dev.ragnarok.fenrir.util.Objects.nonNull;

public class VideosLocalServerFragment extends BaseMvpFragment<VideosLocalServerPresenter, IVideosLocalServerView>
        implements MySearchView.OnQueryTextListener, LocalServerVideosAdapter.VideoOnClickListener, IVideosLocalServerView {
    private final AppPerms.doRequestPermissions requestWritePermission = AppPerms.requestPermissions(this,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
            () -> CustomToast.CreateCustomToast(requireActivity()).showToast(R.string.permission_all_granted_text));
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LocalServerVideosAdapter mAudioRecyclerAdapter;

    public static VideosLocalServerFragment newInstance(int accountId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        VideosLocalServerFragment fragment = new VideosLocalServerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_local_server_video, container, false);

        MySearchView searchView = root.findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(this);
        searchView.setRightButtonVisibility(false);
        searchView.setLeftIcon(R.drawable.magnify);
        searchView.setQuery("", true);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh(false));
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        int columns = requireActivity().getResources().getInteger(R.integer.videos_column_count);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(Constants.PICASSO_TAG));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });
        mAudioRecyclerAdapter = new LocalServerVideosAdapter(requireActivity(), Collections.emptyList());
        mAudioRecyclerAdapter.setVideoOnClickListener(this);
        recyclerView.setAdapter(mAudioRecyclerAdapter);
        return root;
    }

    @NotNull
    @Override
    public IPresenterFactory<VideosLocalServerPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new VideosLocalServerPresenter(
                requireArguments().getInt(Extra.ACCOUNT_ID),
                saveInstanceState
        );
    }

    @Override
    public void displayList(List<Video> videos) {
        if (nonNull(mAudioRecyclerAdapter)) {
            mAudioRecyclerAdapter.setData(videos);
        }
    }

    @Override
    public void notifyListChanged() {
        if (nonNull(mAudioRecyclerAdapter)) {
            mAudioRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayLoading(boolean loading) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setRefreshing(loading);
        }
    }

    @Override
    public void notifyItemChanged(int index) {
        if (nonNull(mAudioRecyclerAdapter)) {
            mAudioRecyclerAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void notifyDataAdded(int position, int count) {
        if (nonNull(mAudioRecyclerAdapter)) {
            mAudioRecyclerAdapter.notifyItemRangeInserted(position, count);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        getPresenter().fireSearchRequestChanged(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        getPresenter().fireSearchRequestChanged(newText);
        return false;
    }

    @Override
    public void onVideoClick(int position, Video video) {
        PlaceFactory.getVkInternalPlayerPlace(video, InternalVideoSize.SIZE_720, true).tryOpenWith(requireActivity());
    }

    @Override
    public void onRequestWritePermissions() {
        requestWritePermission.launch();
    }
}
