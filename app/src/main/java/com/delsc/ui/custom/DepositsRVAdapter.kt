package com.delsc.ui.custom

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.delsc.R
import com.delsc.model.Deposit
import com.delsc.viewmodel.DepositsViewModel
import com.delsc.viewmodel.alternatingColor1
import com.delsc.viewmodel.displayDelDateFormat
import com.delsc.viewmodel.simpleDateFormat

class DepositsRVAdapter(var deposits: List<Pair<String, Deposit>>, val depositsViewModel: DepositsViewModel): RecyclerView.Adapter<DepositRVViewHolder>() {

    private var enableDeletionMode: Boolean = false

    override fun getItemCount(): Int = deposits.size

    @SuppressLint("NotifyDataSetChanged")
    fun changeOrdersList(deposits: List<Pair<String, Deposit>>) {
        this.deposits = deposits
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun enableDeletionMode(enable: Boolean) {
        enableDeletionMode = enable
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepositRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.deposit_rv_view_holder, parent, false)

        return DepositRVViewHolder(view)
    }

    override fun onBindViewHolder(holder: DepositRVViewHolder, position: Int) {

        val deposit = deposits.get(position).second

        holder.depositedDatetimeTV.text = displayDelDateFormat.format(simpleDateFormat.parse(deposit.depositedDatetime!!)!!)
        holder.amountTV.text = "$${deposit.amount}"
        holder.statusTV.text = deposit.status.getShortName()
        holder.statusTV.setBackgroundColor(deposit.status.getColor(holder.itemView.context))

        holder.earningTakenTV.text = "$${deposit.earningTaken}"
        holder.pettyCastTV.text = "$${deposit.pettyCash}"
        holder.previousBalTV.text = "$${deposit.previousBalance}"
        holder.currentCollectionTV.text = "$${deposit.newlyCollectedToDateAmount}"
        holder.currentBalTV.text = "$${deposit.currentBalance}"

        holder.deleteBtn.setOnClickListener {
            val result = depositsViewModel.delete(deposits.get(position).first, deposit)
            if (result.first != 0) {
                Toast.makeText(holder.itemView.context, result.second, Toast.LENGTH_LONG).show()
            }
        }
        if (enableDeletionMode) {
            holder.deleteBtn.visibility = View.VISIBLE
        } else {
            holder.deleteBtn.visibility = View.GONE
        }

        val bgDrawable = holder.itemView.background as GradientDrawable
        if (position % 2 == 0)
            bgDrawable.run {
                mutate()
                colors =
                    intArrayOf(Color.CYAN, Color.CYAN)
            }
        else
            bgDrawable.run {
                mutate()
                colors =
                    intArrayOf(alternatingColor1, alternatingColor1)
            }

        holder.itemView.background = bgDrawable

    }
}

class DepositRVViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val depositedDatetimeTV: TextView = itemView.findViewById(R.id.depositedDatetimeSpn)
    val amountTV: TextView = itemView.findViewById(R.id.amountTV)
    val statusTV: TextView = itemView.findViewById(R.id.statusTV)

    val earningTakenTV: TextView = itemView.findViewById(R.id.earningTakenTV)
    val pettyCastTV: TextView = itemView.findViewById(R.id.pettyCastTV)
    val previousBalTV: TextView = itemView.findViewById(R.id.previousBalTV)
    val currentCollectionTV: TextView = itemView.findViewById(R.id.currentCollectionTV)
    val currentBalTV: TextView = itemView.findViewById(R.id.currentBalTV)
    val deleteBtn: TextView = itemView.findViewById(R.id.deleteBtn)
}