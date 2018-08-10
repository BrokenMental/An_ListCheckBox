package cs.inhatc.jinuk.list_checkbox_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int totalSelected = 0;
    EditText editView;
    ListView listView;
    TextView textView;
    MyListAdapterMy listadapter;
    ArrayList<MyListItem> listAllItems = new ArrayList<MyListItem>();
    ArrayList<MyListItem> listDispItems = new ArrayList<MyListItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView_debug);
        setupList();
        setupAdapter();
        setupFilter();
    }

    private void setupList() {
        listView = findViewById(R.id.list1);
    }

    public class MyListItem {
        int selectedNumber;
        boolean checked;
        String name;
    }

    public class MyListAdapterMy extends MyArrayAdapter<MyListItem> {

        public MyListAdapterMy(Context context) {
            super(context, R.layout.list_item);
            totalSelected = 0;
            setSource(listDispItems);
        }

        @Override
        public void bindView(View view, MyListItem item) {
            TextView name = view.findViewById(R.id.name_saved);
            name.setText(item.name);
            CheckBox cb = view.findViewById(R.id.checkBox_saved);
            cb.setChecked(item.checked);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retView = super.getView(position, convertView, parent);
            final int pos = position;
            final View parView = retView;
            CheckBox cb = retView.findViewById(R.id.checkBox_saved);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyListItem item = listDispItems.get(pos);
                    item.checked = !item.checked;
                    if (item.checked) totalSelected++;
                    item.selectedNumber = totalSelected;
                    Toast.makeText(MainActivity.this, "1: Click " + pos + "th " + item.checked + " " + totalSelected, Toast.LENGTH_SHORT).show();
                    printDebug();
                }
            });
            TextView name = retView.findViewById(R.id.name_saved);
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyListItem item = listDispItems.get(pos);
                    item.checked = !item.checked;
                    if (item.checked) totalSelected++;
                    item.selectedNumber = totalSelected;
                    Toast.makeText(MainActivity.this, "2: Click " + pos + "th " + item.checked + " " + totalSelected, Toast.LENGTH_SHORT).show();
                    printDebug();
                    bindView(parView, item);
                }
            });

            return retView;
        }

        public void fillter(String searchText) {
            listDispItems.clear();
            totalSelected = 0;
            for (int i = 0; i < listAllItems.size(); i++) {
                MyListItem item = listAllItems.get(i);
                item.checked = false;
                item.selectedNumber = 0;
            }
            if (searchText.length() == 0) {
                listDispItems.addAll(listAllItems);
            } else {
                for (MyListItem item : listAllItems) {
                    if (item.name.contains(searchText)) {
                        listDispItems.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }


    public class AdapterAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog mDlg;
        Context mContext;

        public AdapterAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDlg = new ProgressDialog(mContext);
            mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDlg.setMessage("loading");
            mDlg.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            // listAllItems MyListItem
            listAllItems.clear();
            listDispItems.clear();
            List<String> list = new ArrayList<String>();

            // Test item
            for (int i = 0; i < 100; i++) {
                list.add("TEST test" + i);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Test item

            if (list == null) list = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {
                MyListItem item = new MyListItem();
                item.checked = false;
                item.name = list.get(i);
                listAllItems.add(item);
            }

            if (listAllItems != null) {
                Collections.sort(listAllItems, nameComparator);
            }
            listDispItems.addAll(listAllItems);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mDlg.dismiss();
            listadapter = new MyListAdapterMy(mContext);
            listView.setAdapter(listadapter);

            String searchText = editView.getText().toString();
            if (listadapter != null) listadapter.fillter(searchText);
        }

        private final Comparator<MyListItem> nameComparator
                = new Comparator<MyListItem>() {
            public final int
            compare(MyListItem a, MyListItem b) {
                return collator.compare(a.name, b.name);
            }

            private final Collator collator = Collator.getInstance();
        };
    }

    private void setupAdapter() {
        AdapterAsyncTask adaterAsyncTask = new AdapterAsyncTask(MainActivity.this);
        adaterAsyncTask.execute();
    }

    private void setupFilter() {
        editView = findViewById(R.id.savedfilter);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = editView.getText().toString();
                if (listadapter != null) listadapter.fillter(searchText);
            }
        });
    }

    private int getSelectedItemCount() {
        int checkcnt = 0;
        for (int i = 0; i < listDispItems.size(); i++) {
            MyListItem item = listDispItems.get(i);
            if (item.checked) checkcnt++;
        }
        return checkcnt;
    }

    private List<String> getSelectedItems() {
        List<String> ret = new ArrayList<String>();
        int count = 0;
        for (int i = 0; i < listDispItems.size(); i++) {
            MyListItem item = listDispItems.get(i);
            if (item.checked) {
                if (count < item.selectedNumber) {
                    count = item.selectedNumber;
                }
            }
        }
        for (int j = 1; j <= count; j++) {
            for (int i = 0; i < listDispItems.size(); i++) {
                MyListItem item = listDispItems.get(i);
                if (item.checked && item.selectedNumber == j) {
                    ret.add(item.name);
                }
            }
        }
        return ret;
    }

    private String getSelectedItem() {
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < listDispItems.size(); i++) {
            MyListItem item = listDispItems.get(i);
            if (item.checked) {
                return item.name;
            }
        }
        return "";
    }

    private void printDebug() {
        StringBuilder sb = new StringBuilder();
        sb.append("Count:" + getSelectedItemCount() + "\n");
        sb.append("getSelectedItem:" + getSelectedItem() + "\n");
        sb.append("getSelectedItems:");
        List<String> data = getSelectedItems();
        for (int i = 0; i < data.size(); i++) {
            String item = data.get(i);
            sb.append(item + ",");
        }
        textView.setText(sb.toString());
    }
}
