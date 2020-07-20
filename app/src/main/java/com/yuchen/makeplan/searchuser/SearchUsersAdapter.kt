package com.yuchen.makeplan.searchuser

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.databinding.ItemUserBinding

class SearchUsersAdapter() : RecyclerView.Adapter<SearchUsersAdapter.UserHolder>(), Filterable {

    private var userSourceList: List<User>? = null
    private var userFilteredList: List<User>? =null

    fun appendList(userList: List<User>) {
        this.userSourceList = userList
        this.userFilteredList = userList
    }

    private var onSelectListener: OnSelectListener? = null
    interface OnSelectListener{
        fun userSelect(user: User)
    }
    fun setOnSelectListener(onSelectListener: OnSelectListener){
        this.onSelectListener = onSelectListener
    }

    inner class UserHolder(var binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
            binding.itemUserLayout.setOnClickListener {
                onSelectListener?.userSelect(user)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        return UserHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        userFilteredList?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return userFilteredList?.let {it.size} ?: 0
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filteredList : List<User>?
                val charString: String = constraint.toString()
                if (charString.isEmpty()) {
                    filteredList = userSourceList
                } else {
                    filteredList = userSourceList?.filter {
                        it.displayName.toUpperCase().contains(charString.toUpperCase()) || it.email.toUpperCase().contains(charString.toUpperCase())
                    }
                }
                val filterResults: FilterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                userFilteredList = results?.values as List<User>
                notifyDataSetChanged()
            }
        }
    }
}