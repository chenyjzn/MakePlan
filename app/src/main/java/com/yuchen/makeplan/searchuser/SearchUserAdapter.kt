package com.yuchen.makeplan.searchuser

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.data.MultiProject
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.databinding.ItemUserBinding

class SearchUserAdapter(private val viewModel: SearchUserViewModel) : RecyclerView.Adapter<SearchUserAdapter.UserHolder>(), Filterable {

    private var userSourceList: List<User>? = null
    private var userFilteredList: List<User>? = null

    fun appendList(list: List<User>, project: MultiProject) {
        this.userSourceList = getExcludeUserList(list, project)
        this.userFilteredList = userSourceList
    }

    private fun getExcludeUserList(list: List<User>, project: MultiProject): List<User> {
        return list.filter {
            var isNotMember = true
            for (i in project.receiveUid) {
                if (i == it.uid) {
                    isNotMember = false
                    break
                }
            }
            if (isNotMember) {
                for (i in project.membersUid) {
                    if (i == it.uid) {
                        isNotMember = false
                        break
                    }
                }
            }
            if (isNotMember) {
                for (i in project.sendUid) {
                    if (i == it.uid) {
                        isNotMember = false
                        break
                    }
                }
            }
            isNotMember
        }
    }

    private var onSelectListener: OnSelectListener? = null

    interface OnSelectListener {
        fun userSelect(user: User)
    }

    fun setOnSelectListener(onSelectListener: OnSelectListener) {
        this.onSelectListener = onSelectListener
    }

    inner class UserHolder(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {
        fun bind(user: User) {
            viewModel.loadingStatus.observe(this, Observer {
                when (it) {
                    is LoadingStatus.LOADING -> {
                        binding.itemUserCard.isEnabled = false
                    }
                    is LoadingStatus.DONE -> {
                        binding.itemUserCard.isEnabled = true
                    }
                    is LoadingStatus.ERROR -> {

                    }
                }
            })
            binding.user = user
            binding.itemUserCard.setOnClickListener {
                onSelectListener?.userSelect(user)
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        return UserHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        userFilteredList?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return userFilteredList?.let { it.size } ?: 0
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filteredList: List<User>?
                val charString: String = constraint.toString()
                if (charString.isEmpty()) {
                    filteredList = userSourceList
                } else {
                    filteredList = userSourceList?.filter {
                        it.displayName.toUpperCase()
                            .contains(charString.toUpperCase()) || it.email.toUpperCase()
                            .contains(charString.toUpperCase())
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

    override fun onViewAttachedToWindow(holder: UserHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: UserHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }
}