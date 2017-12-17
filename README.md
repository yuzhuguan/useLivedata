# useLivedata
老项目使用ViewModel , LiveData , 项目确实简单，易于维护了。下面会使用room dragger2

## 现在已经彻底完成了ViewModel部分代码
![](final-architecture.png)

### 核心代
```java
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
```
