package com.yuchen.makeplan.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.ItemMultiProjectBinding
import com.yuchen.makeplan.util.TimeUtil.StampToDate
import com.yuchen.makeplan.util.UserManager

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.MultiProjectHolder>(),Filterable {

    private var projectSourceList: List<MultiProject>? = null
    private var projectFilteredList: List<MultiProject>? =null

    fun appendList(projectList: List<MultiProject>) {
        this.projectSourceList = projectList
        this.projectFilteredList = projectList
    }

    private var onClickListener: OnClickListener? = null
    fun setItemClickListener(itemClickListener: OnClickListener?) {
        onClickListener = itemClickListener
    }

    interface OnClickListener {
        fun onProjectClick(project: MultiProject)
        fun onProjectLongClick(project: MultiProject)
    }

    inner class MultiProjectHolder(var binding: ItemMultiProjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(project: MultiProject) {
            binding.project = project
            binding.itemProjectCard.setOnClickListener {
                onClickListener?.onProjectClick(project)
            }
            binding.itemProjectCard.setOnLongClickListener {
                onClickListener?.onProjectLongClick(project)
                true
            }
            binding.itemProjectEditTime.text = StampToDate(project.updateTime)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiProjectHolder {
        return MultiProjectHolder(ItemMultiProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MultiProjectHolder, position: Int) {
        projectFilteredList?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return projectFilteredList?.let {it.size} ?: 0
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filteredList : List<MultiProject>?
                val charString: String = constraint.toString()
                if (charString.isEmpty()) {
                    filteredList = projectSourceList?.filter {
                        var notMember = true
                        for (i in it.members){
                            if (i.uid == UserManager.user.uid) {
                                notMember = false
                                break
                            }
                        }
                        notMember
                    }
                } else {
                    filteredList = projectSourceList?.filter {
                        var haveProjectName = it.name.toUpperCase().contains(charString.toUpperCase())
                        var haveUser = false
                        var notMember = true
                        for (i in it.members){
                            if (i.uid == UserManager.user.uid) {
                                notMember = false
                                break
                            }
                            haveUser = haveUser || i.displayName.toUpperCase().contains(charString.toUpperCase()) || i.email.toUpperCase().contains(charString.toUpperCase())
                        }
                        (haveUser||haveProjectName)&&notMember
                    }
                }
                val filterResults: FilterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                projectFilteredList = results?.values as List<MultiProject>
                notifyDataSetChanged()
            }
        }
    }
}