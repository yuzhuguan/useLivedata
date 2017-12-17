package com.codef1.oldcode;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PWViewModel mViewModel;
    private List<Password> mData;
    private PWAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ViewModel
        mViewModel = ViewModelProviders.of(this).get(PWViewModel.class);

        final RecyclerView recyclerView = findViewById(R.id.rvList);
        final Observer<List<Password>> observer = new Observer<List<Password>>() {
            @Override
            public void onChanged(@Nullable final List<Password> data) {
                if (mData == null) {
                    mData = data;
                    mAdapter = new PWAdapter();
                    recyclerView.setAdapter(mAdapter);
                } else {
                    DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {

                        @Override
                        public int getOldListSize() {
                            return mData.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return data.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            return (mData.get(oldItemPosition)).getID() ==
                                    data.get(newItemPosition).getID();
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            Password oldPw = mData.get(oldItemPosition);
                            Password newPw = data.get(newItemPosition);
                            return oldPw.equals(newPw);
                        }
                    });
                    result.dispatchUpdatesTo(mAdapter);
                    mData = data;
                }
            }
        };
        mViewModel.getData().observe(this, observer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_pw:
                final LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                final EditText titleEditText = new EditText(this);
                final EditText valueEditText = new EditText(this);
                titleEditText.setHint("密码名");
                valueEditText.setHint("密码值");
                linearLayout.addView(titleEditText);
                linearLayout.addView(valueEditText);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("添加新的密码")
                        .setView(linearLayout)
                        .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = String.valueOf(titleEditText.getText());
                                String value = valueEditText.getText().toString();
                                mViewModel.addPassword(title, value);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class PWAdapter extends RecyclerView.Adapter<PWAdapter.PWHolder>{
        @Override
        public PWHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new PWHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PWHolder holder, int position) {
            Password password = mData.get(position);
            holder.getTitleTextView().setText(password.getTitle());
            holder.getValueTextView().setText(password.getValue());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class PWHolder extends RecyclerView.ViewHolder{

            private final TextView mTvTitle;
            private final TextView mTvValue;

            PWHolder(View itemView) {
                super(itemView);
                mTvTitle = itemView.findViewById(R.id.tvTitle);
                mTvValue = itemView.findViewById(R.id.tvValue);
                Button btnDelete = itemView.findViewById(R.id.btnDelete);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition();
                        Password password = mData.get(pos);
                        mViewModel.removePassword(password.getID());
                    }
                });
            }

            TextView getTitleTextView() {
                return mTvTitle;
            }

            TextView getValueTextView() {
                return mTvValue;
            }
        }
    }


}
