package com.chatapp.android.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;

import java.util.ArrayList;

public class CPAdapter extends BaseAdapter {


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.country_list, parent, false);
        AvnNextLTProRegTextView country_name = (AvnNextLTProRegTextView) row.findViewById(R.id.countryName_ll);
        AvnNextLTProRegTextView country_code = (AvnNextLTProRegTextView) row.findViewById(R.id.countryCode_ll);
        country_name.setText(list.get(position));
        country_code.setText(codelist.get(position));
        return row;
    }


    public CPAdapter(Context c, ArrayList<String> list, ArrayList<String> codelist) {
        this.context = c;
        this.list = list;
        this.codelist = codelist;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

	/*@Override
    public Filter getFilter() {
		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				// ArrayList<SingleRow> tempList = new ArrayList<SingleRow>();

				System.out.println("MAIN LIST =" + list.size());

				if (constraint == null || constraint.length() == 0) {
					// No filter implemented we return all the list
					
					 * list.clear(); for (int i = 0; i < 20; i++) { list.add(new
					 * SingleRow(country_list[i], country_code[i])); }
					 

					results.values = list;
					results.count = list.size();
				} else {
					// We perform filtering operation

					List<SingleRow> nPlanetList = new ArrayList<SingleRow>();
					nPlanetList.clear();

					for (SingleRow p : list) {

						if (p.country_list.toLowerCase().startsWith(
								constraint.toString())) {
							nPlanetList.add(p);
							results.values = nPlanetList;
							results.count = nPlanetList.size();
							
							 * } else if (constraint.length() == 0) {
							 * results.values = list; // return results;
							 * results.count = list.size(); }
							 

						}

					}

					
					 * if (constraint.length() == 0) { results.values = list;
					 * results.count = list.size(); }
					 
				}

				return results;

			}

			@Override
			protected void publishResults(CharSequence constraint,FilterResults results) {

				System.out.println("constraint =" + constraint);
				System.out.println("constraint Length =" + constraint.length());
				// results.count
				if (results.count == 0)
				{
					//list = list;
					ChooseCountry.cpa.notifyDataSetChanged();
					//notifyDataSetInvalidated();
				}
				else 
				{

					list = (ArrayList<SingleRow>) results.values;
					ChooseCountry.cpa.notifyDataSetChanged();
				}

			}
			
			 * @Override protected void publishResults(CharSequence
			 * constraint,FilterResults results) { //
			 * stub Names = (ArrayList<String>) results.values;
			 * notifyDataSetChanged(); }
			 

			
			 * @Override protected FilterResults performFiltering(CharSequence
			 * constraint) { FilterResults result = new FilterResults(); if
			 * (!TextUtils.isEmpty(constraint)) { ArrayList<type> foundItems =
			 * new ArrayList<type>(); //do something //filter resutls }
			 * 
			 * result.count = foundItems.size();//search results found return
			 * count result.values = foundItems;// return values
			 * 
			 * else { result.count=-1;// no search results found } return
			 * result; }
			 

		};
	}
}

class SingleRow 
{
	 String country_list;
	 String country_code;

	public SingleRow(String country_list, String country_code) {
		this.country_list = country_list;
		this.country_code = country_code;
	}*/

    ArrayList<String> list;
    ArrayList<String> codelist;
    Context context;

}
