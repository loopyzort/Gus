package com.loopyzort.gus.presentation.search;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopyzort.gus.R;
import com.loopyzort.gus.data.remote.model.User;
import com.loopyzort.gus.injection.Injection;
import com.squareup.picasso.Picasso;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserSearchActivity extends AppCompatActivity implements UserSearchContract.View {
    private UserSearchPresenter userSearchPresenter;
    private Toolbar toolbar;
    private SearchView searchView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView textViewErrorMessage;
    private UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        userSearchPresenter = new UserSearchPresenter(Injection.provideUserRepo(),
                Schedulers.io(), AndroidSchedulers.mainThread());
        userSearchPresenter.attachView(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        textViewErrorMessage = (TextView) findViewById(R.id.text_view_error_msg);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_users);
        usersAdapter = new UsersAdapter(null, this);
        recyclerView.setAdapter(usersAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_user_search, menu);
        final MenuItem searchActionMenuItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                userSearchPresenter.search(query);
                toolbar.setTitle(query);
                searchActionMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchActionMenuItem.expandActionView();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userSearchPresenter.detachView();
    }

    @Override
    public void showSearchResults(List<User> githubUserList) {
        recyclerView.setVisibility(View.VISIBLE);
        textViewErrorMessage.setVisibility(View.GONE);
        usersAdapter.setItems(githubUserList);
    }

    @Override
    public void showError(String message) {
        textViewErrorMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        textViewErrorMessage.setText(message);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        textViewErrorMessage.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        textViewErrorMessage.setVisibility(View.GONE);
    }

    class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> {
        private final Context context;
        private List<User> items;

        UsersAdapter(List<User> items, Context context) {
            this.items = items;
            this.context = context;
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
            return new UserViewHolder(v);
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            User item = items.get(position);

            holder.textViewBio.setText(item.getBio());
            if (item.getName() != null) {
                holder.textViewName.setText(item.getLogin() + " - " + item.getName());
            } else {
                holder.textViewName.setText(item.getLogin());
            }
            Picasso.with(context).load(item.getAvatarUrl()).into(holder.imageViewAvatar);
        }

        @Override
        public int getItemCount() {
            if (items == null) {
                return 0;
            }
            return items.size();
        }

        void setItems(List<User> githubUserList) {
            this.items = githubUserList;
            notifyDataSetChanged();
        }
    }


    class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewBio;
        final TextView textViewName;
        final ImageView imageViewAvatar;

        UserViewHolder(View v) {
            super(v);
            imageViewAvatar = (ImageView) v.findViewById(R.id.imageview_userprofilepic);
            textViewName = (TextView) v.findViewById(R.id.textview_username);
            textViewBio = (TextView) v.findViewById(R.id.textview_user_profile_info);
        }
    }

}
