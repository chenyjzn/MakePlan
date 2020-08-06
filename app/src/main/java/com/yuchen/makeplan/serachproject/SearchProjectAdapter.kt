package com.yuchen.makeplan.serachproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.MainActivity
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.databinding.ItemMultiProjectBinding
import com.yuchen.makeplan.util.TimeUtil.stampToDate
import com.yuchen.makeplan.util.UserManager

class SearchProjectAdapter (private val viewModel: SearchProjectViewModel) : RecyclerView.Adapter<SearchProjectAdapter.MultiProjectHolder>(),
    Filterable {

    private var projectSourceList: List<MultiProject>? = null
    private var projectFilteredList: List<MultiProject>? = null

    fun appendList(projectList: List<MultiProject>) {
        val excludeSource = projectList.filter {
            var isNotMember = true
            for (i in it.membersUid) {
                if (i == UserManager.user.uid) {
                    isNotMember = false
                    break
                }
            }
            if (isNotMember) {
                for (i in it.sendUid) {
                    if (i == UserManager.user.uid) {
                        isNotMember = false
                        break
                    }
                }
            }
            if (isNotMember) {
                for (i in it.receiveUid) {
                    if (i == UserManager.user.uid) {
                        isNotMember = false
                        break
                    }
                }
            }
            isNotMember
        }
        this.projectSourceList = excludeSource
        this.projectFilteredList = excludeSource
    }

    private var onClickListener: OnClickListener? = null
    fun setItemClickListener(itemClickListener: OnClickListener?) {
        onClickListener = itemClickListener
    }

    interface OnClickListener {
        fun onProjectClick(project: MultiProject)
        fun onProjectLongClick(project: MultiProject)
    }

    inner class MultiProjectHolder(var binding: ItemMultiProjectBinding) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {
        fun bind(project: MultiProject) {
            viewModel.loadingStatus.observe(this, Observer {
                when (it) {
                    is LoadingStatus.LOADING -> {
                        binding.itemMultiProjectCard.isEnabled = false
                    }
                    is LoadingStatus.DONE -> {
                        binding.itemMultiProjectCard.isEnabled = true
                    }
                    is LoadingStatus.ERROR -> {

                    }
                }
            })

            binding.project = project
            binding.itemMultiProjectCard.setOnClickListener {
                onClickListener?.onProjectClick(project)
            }
            binding.itemMultiProjectCard.setOnLongClickListener {
                onClickListener?.onProjectLongClick(project)
                true
            }
            binding.itemMultiProjectEditTime.text = stampToDate(project.updateTime)
            binding.executePendingBindings()
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun onAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
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
        return projectFilteredList?.let { it.size } ?: 0
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filteredList: List<MultiProject>?
                val charString: String = constraint.toString()
                if (charString.isEmpty()) {
                    filteredList = projectSourceList
                } else {
                    filteredList = projectSourceList?.filter {
                        var haveProjectName =
                            it.name.toUpperCase().contains(charString.toUpperCase())
                        var haveUser = false
                        var notMember = true
                        for (i in it.members) {
                            if (i.uid == UserManager.user.uid) {
                                notMember = false
                                break
                            }
                            haveUser = haveUser || i.displayName.toUpperCase()
                                .contains(charString.toUpperCase()) || i.email.toUpperCase()
                                .contains(charString.toUpperCase())
                        }
                        (haveUser || haveProjectName) && notMember
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

    override fun onViewAttachedToWindow(holder: MultiProjectHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: MultiProjectHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }
}