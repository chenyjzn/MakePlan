package com.yuchen.makeplan.members

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuchen.makeplan.data.User
import com.yuchen.makeplan.databinding.ItemUserBinding

class MembersItemsAdapter() : RecyclerView.Adapter<MembersItemsAdapter.UserHolder>() {

    private var memberList: List<User>? = null

    private var userClickListener: UserClickListener? = null
    interface UserClickListener{
        fun userSelect(user: User)
    }
    fun setUserClickListener(userClickListener: UserClickListener){
        this.userClickListener = userClickListener
    }

    inner class UserHolder(var binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
            binding.itemUserLayout.setOnClickListener {
                userClickListener?.userSelect(user)
            }
            binding.executePendingBindings()
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
        return memberList?.let {it.size} ?: 0
    }

    fun submitList(memberList: List<User>) {
        this.memberList = memberList
        notifyDataSetChanged()
    }
}