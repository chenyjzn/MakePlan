package com.yuchen.makeplan.members

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.LoadingStatus
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.databinding.ItemUserBinding

class MembersItemsAdapter(private val viewModel: MembersItemsViewModel) : RecyclerView.Adapter<MembersItemsAdapter.UserHolder>() {

    private var memberList: List<User>? = null

    private var userClickListener: UserClickListener? = null

    interface UserClickListener {
        fun userSelect(user: User)
    }

    fun setUserClickListener(userClickListener: UserClickListener) {
        this.userClickListener = userClickListener
    }

    inner class UserHolder(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {
        fun bind(user: User) {
            binding.user = user
            viewModel.loadingStatus.observe(this, Observer {
                it?.let {
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
                }
            })
            binding.itemUserCard.setOnClickListener {
                userClickListener?.userSelect(user)
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
        memberList?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {
        return memberList?.let { it.size } ?: 0
    }

    fun submitList(memberList: List<User>) {
        this.memberList = memberList
        notifyDataSetChanged()
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