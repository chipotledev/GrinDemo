package io.chipotie.grindemo.util

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BindViewHolder<T : ViewDataBinding> (val bindObject: T) : RecyclerView.ViewHolder(bindObject.root)