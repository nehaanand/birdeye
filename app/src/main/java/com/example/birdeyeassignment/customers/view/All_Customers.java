package com.example.birdeyeassignment.customers.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.birdeyeassignment.R;
import com.example.birdeyeassignment.checkin.view.CheckIn_Customer;
import com.example.birdeyeassignment.customers.model.All_Customers_Response;
import com.example.birdeyeassignment.job_scheduler.ShowNotificationJob;
import com.example.birdeyeassignment.network.ReUseComponets;
import com.example.birdeyeassignment.network.listeners.Delete_DataListener;
import com.example.birdeyeassignment.delete_customer.Delete_Customer_Response;
import com.example.birdeyeassignment.utils.PaginationScrollListener;
import com.example.birdeyeassignment.utils.Sqlite_Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class All_Customers extends AppCompatActivity {
    private ProgressDialog pdilog;
    private static SharedPreferences defaultSharedPreferences = null;
    MenuItem search, filter, asc, desc;
    RecyclerView rv_customers;
    LinearLayout ll_nodata;
    Sqlite_Database db;
    List<All_Customers_Response> list;
    LinearLayoutManager linearLayoutManager;
    private PaginationScrollListener scrollListener;
    public int start_counter = 0;
    public int limit = 10;

    List_Adapter itemListDataAdapter;

    String sort_type = "ASC";
    public static final int PAGE_START = 1;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage;
    private boolean isLoading = false;
    String search_text = "";
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_customers);
        db = new Sqlite_Database(All_Customers.this);
        ShowNotificationJob.setJobScheduler(All_Customers.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Customers");
        totalPage = db.get_Customers_Count() / PaginationScrollListener.PAGE_SIZE;
        Log.d("totalPage", db.get_Customers_Count() + " ");

        rv_customers = (RecyclerView) findViewById(R.id.rv_customers);
        ll_nodata = (LinearLayout) findViewById(R.id.ll_nodata);
        list = new ArrayList<>();
        list = db.get_All_Customers(start_counter, limit, search_text, sort_type);
        itemListDataAdapter = new List_Adapter(All_Customers.this, list);
        rv_customers.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_customers.setLayoutManager(linearLayoutManager);
        rv_customers.setAdapter(itemListDataAdapter);
        start_counter = start_counter + limit;

        set_scroll();


    }

    @Override
    protected void onRestart() {
        rv_customers.setAdapter(null);
        itemListDataAdapter = new List_Adapter(All_Customers.this, db.get_All_Customers(start_counter, limit, search_text, sort_type));
        rv_customers.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_customers.setLayoutManager(linearLayoutManager);
        rv_customers.setAdapter(itemListDataAdapter);
        start_counter = start_counter + limit;
        set_scroll();
        super.onRestart();
    }

    public void set_scroll() {
        rv_customers.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {

                isLoading = true;
                currentPage++;
                preparedListItem(start_counter, limit);

            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void preparedListItem(final int start, final int limit) {
        Log.d("stcounter", start + " " + limit);
        final ArrayList<All_Customers_Response> items = new ArrayList<>();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list = db.get_All_Customers(start, limit, search_text, sort_type);

                for (int i = 0; i < list.size(); i++) {
                    All_Customers_Response postItem = new All_Customers_Response();

                    postItem.setFirstName(list.get(i).getFirstName());
                    postItem.setLastName(list.get(i).getLastName());
                    postItem.setNumber(list.get(i).getNumber());
                    postItem.setEmailId(list.get(i).getEmailId());
                    items.add(postItem);
                }
                if (currentPage != PAGE_START) itemListDataAdapter.removeLoading();
                itemListDataAdapter.addAll(items);
                if (currentPage < totalPage) itemListDataAdapter.addLoading();
                else isLastPage = true;
                isLoading = false;
                start_counter = start + limit;

            }
        }, 1000);
    }

    public void set_list_data(int start_counter, int limit, List<All_Customers_Response> list, String search_flag) {

        if (search_flag.equalsIgnoreCase("search") && list.size() == 0) {
            ll_nodata.setVisibility(View.VISIBLE);
            rv_customers.setAdapter(null);
        } else {
            ll_nodata.setVisibility(View.GONE);

            itemListDataAdapter = new List_Adapter(All_Customers.this, list);
            rv_customers.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rv_customers.setLayoutManager(linearLayoutManager);
            rv_customers.setAdapter(itemListDataAdapter);
            start_counter = start_counter + limit;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        this.menu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter, menu);
        search = menu.findItem(R.id.app_bar_search);
        filter = menu.findItem(R.id.app_bar_sort);
        asc = menu.findItem(R.id.asc);
        desc = menu.findItem(R.id.desc);

        search.setVisible(true);
        filter.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.app_bar_search);
        final MenuItem sort = menu.findItem(R.id.app_bar_sort);
        MenuItem checkin = menu.findItem(R.id.app_bar_add);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchViewMenuItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search_text = newText;
                list = new ArrayList<>();

                list = db.get_All_Customers(0, limit, search_text, sort_type);
                set_list_data(0, limit, list, "search");
                return false;
            }
        });
        asc.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sort_type = "ASC";
                db.get_All_Customers(start_counter, limit, search_text, sort_type);
                return false;
            }
        });
        desc.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sort_type = "DESC";
                db.get_All_Customers(start_counter, limit, search_text, sort_type);
                return false;
            }
        });


        checkin.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(All_Customers.this, CheckIn_Customer.class);
                startActivity(i);
                return false;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public class List_Adapter extends RecyclerView.Adapter<List_Adapter.SingleItemRowHolder> {

        private ArrayList<HashMap<String, String>> itemsList;
        private Context mContext;
        private List<All_Customers_Response> mPostItems;
        private List<All_Customers_Response> mPostItemsOriginal;
        private boolean isLoaderVisible = false;

        private static final int VIEW_TYPE_LOADING = 0;
        private static final int VIEW_TYPE_NORMAL = 1;
        ArrayList<HashMap<String, String>> modelList;

        public List_Adapter(Context context, List<All_Customers_Response> mPostItems) {
            this.mContext = context;
            this.modelList = modelList;
            this.mPostItems = mPostItems;
            this.mPostItemsOriginal = new ArrayList<>();
            this.mPostItemsOriginal.addAll(mPostItems);

        }

        @Override
        public List_Adapter.SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_all_customers, null);
            List_Adapter.SingleItemRowHolder mh = new List_Adapter.SingleItemRowHolder(v, i);
            return mh;
        }

        @Override
        public void onBindViewHolder(final List_Adapter.SingleItemRowHolder holder, final int i) {
            try {

                if (mPostItems.get(i).getFirstName() != null) {
                    holder.tv_name.setText("Name: " + mPostItems.get(i).getFirstName());
                } else if (mPostItems.get(i).getLastName() != null) {
                    holder.tv_name.setText("Name: " + mPostItems.get(i).getLastName());
                } else {
                    holder.tv_name.setText("Name: " + mPostItems.get(i).getEmailId().split("@")[0]);
                }
                holder.tv_email.setText("Email ID: " + mPostItems.get(i).getEmailId());

                holder.tv_phone.setText("Phone: " + mPostItems.get(i).getPhone());
                holder.tv_popup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(mContext, holder.tv_popup);
                        popup.inflate(R.menu.delete_csutomer);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {

                                    case R.id.delete:

                                        AlertDialog.Builder alert = new AlertDialog.Builder(All_Customers.this);
                                        alert.setTitle("Delete Customer");
                                        alert.setMessage("Are you sure you want to delete?");
                                        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                pdilog = new ProgressDialog(All_Customers.this, R.style.CustomProgress);
                                                pdilog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                pdilog.getWindow().setGravity(Gravity.CENTER);
                                                pdilog.show();
                                                delete_customer(i);
                                            }
                                        });
                                        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // close dialog
                                                dialog.cancel();
                                            }
                                        });
                                        alert.show();
                                        break;

                                }
                                return false;
                            }
                        });
                        //displaying the popup
                        popup.show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void delete_customer(final int i) {
            ReUseComponets servercall = new ReUseComponets(new Delete_DataListener() {

                @Override
                public void showDeleteResult(int code, Delete_Customer_Response result) {
                    pdilog.dismiss();
                    if (code == 200) {
                        db.delete_Customer(mPostItems.get(i).getNumber());
                        Toast.makeText(All_Customers.this, "Customer Deleted Successfully", Toast.LENGTH_LONG).show();
                        mPostItems.remove(i);
                        notifyDataSetChanged();

                    } else if (code == 400) {
                        Toast.makeText(All_Customers.this, "Some Error occured", Toast.LENGTH_LONG).show();

                    } else {

                    }
                }

                @Override
                public void showError(Throwable error) {
                    Log.d("Error", "" + error);
                }
            });

            servercall.asynCallDeleteCustomer(Integer.parseInt(mPostItems.get(i).getNumber()));
        }

        @Override
        public int getItemViewType(int position) {
            if (isLoaderVisible) {
                return position == mPostItems.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
            } else {
                return VIEW_TYPE_NORMAL;
            }
        }

        public void add(All_Customers_Response response) {
            mPostItems.add(response);
            notifyItemInserted(mPostItems.size() - 1);
        }

        public void addAll(List<All_Customers_Response> postItems) {
            for (All_Customers_Response response : postItems) {
                add(response);
            }
        }


        private void remove(All_Customers_Response postItems) {
            int position = mPostItems.indexOf(postItems);
            if (position > -1) {
                mPostItems.remove(position);
                notifyItemRemoved(position);
            }
        }

        public void addLoading() {
            isLoaderVisible = true;
            add(new All_Customers_Response());
        }

        public void removeLoading() {
            isLoaderVisible = false;
            int position = mPostItems.size() - 1;
            All_Customers_Response item = getItem(position);
            if (item != null) {
                mPostItems.remove(position);
                notifyItemRemoved(position);
            }
        }

        public void clear() {
            while (getItemCount() > 0) {
                remove(getItem(0));
            }
        }

        All_Customers_Response getItem(int position) {
            return mPostItems.get(position);
        }

        @Override
        public int getItemCount() {
            return (null != mPostItems ? mPostItems.size() : 0);
        }


        public class SingleItemRowHolder extends RecyclerView.ViewHolder {

            public TextView tv_name, tv_popup, tv_email, tv_phone;

            public SingleItemRowHolder(View view, final int position) {
                super(view);

                this.tv_name = (TextView) view.findViewById(R.id.tv_name);
                this.tv_popup = (TextView) view.findViewById(R.id.tv_popup);
                this.tv_email = (TextView) view.findViewById(R.id.tv_email);
                this.tv_phone = (TextView) view.findViewById(R.id.tv_phone);

            }
        }


    }

}
