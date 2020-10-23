package dev.ragnarok.fenrir.modalbottomsheetdialogfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.ragnarok.fenrir.R
import dev.ragnarok.fenrir.picasso.PicassoInstance
import dev.ragnarok.fenrir.picasso.transforms.PolyTransformation
import dev.ragnarok.fenrir.settings.CurrentTheme
import dev.ragnarok.fenrir.util.Utils

class ModalBottomSheetDialogFragment(listener: Listener) : BottomSheetDialogFragment() {

    companion object {

        private const val KEY_EXCLUDES = "excludes"
        private const val KEY_OPTIONS = "options"
        private const val KEY_COLUMNS = "columns"
        private const val KEY_HEADER = "header"
        private const val KEY_DRAWABLE_RES = "resource"
        private const val KEY_IMAGE_URL = "url"

        private fun newInstance(listener: Listener, builder: Builder): ModalBottomSheetDialogFragment {
            val fragment = ModalBottomSheetDialogFragment(listener)
            val args = Bundle()
            args.putParcelableArrayList(KEY_OPTIONS, builder.options)
            args.putInt(KEY_COLUMNS, builder.columns)
            args.putInt(KEY_DRAWABLE_RES, builder.icon)
            args.putIntegerArrayList(KEY_EXCLUDES, builder.excludes)
            args.putString(KEY_HEADER, builder.header)
            args.putString(KEY_IMAGE_URL, builder.urlicon)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var list: RecyclerView
    private lateinit var adapter: Adapter
    private var listener: Listener? = null

    private val menuInflater by lazy {
        MenuInflater(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.modal_bottom_sheet_dialog_fragment, container, false)
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = view.findViewById(R.id.list)
        val arguments = arguments
                ?: throw IllegalStateException("You need to create this via the builder")

        val optionHolders = arguments.getParcelableArrayList<OptionHolder>(KEY_OPTIONS)!!
        val excludes = arguments.getIntegerArrayList(KEY_EXCLUDES)!!

        val options = mutableListOf<Option>()

        val final_options = mutableListOf<Option>()

        optionHolders.forEach {
            val resource = it.resource
            val optionRequest = it.optionRequest
            if (resource != null) {
                inflate(resource, options)
            }
            if (optionRequest != null) {
                options.add(optionRequest.toOption(requireContext()))
            }
        }

        options.forEach {
            if (!excludes.contains(it.id))
                final_options.add(it)
        }

        adapter = Adapter {
            listener?.onModalOptionSelected(it)
            dismissAllowingStateLoss()
        }
        adapter.header = arguments.getString(KEY_HEADER)
        adapter.icon = arguments.getInt(KEY_DRAWABLE_RES)
        adapter.urlicon = arguments.getString(KEY_IMAGE_URL)

        list.adapter = adapter
        val columns = arguments.getInt(KEY_COLUMNS)
        if (columns == 1) {
            list.layoutManager = LinearLayoutManager(context)
        } else {
            val layoutManager = GridLayoutManager(context, columns)
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.header != null && position == 0) {
                        columns
                    } else {
                        1
                    }
                }
            }
            list.layoutManager = layoutManager
        }

        adapter.set(final_options)
    }

    @SuppressLint("RestrictedApi")
    private fun inflate(menuRes: Int, options: MutableList<Option>) {
        val menu = MenuBuilder(context)
        menuInflater.inflate(menuRes, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val option = Option(item.itemId, item.title, item.icon)
            options.add(option)
        }
    }

    /**
     * Used to build a [ModalBottomSheetDialogFragment]
     */
    class Builder {

        internal var options = ArrayList<OptionHolder>()
        internal var excludes = ArrayList<Int>()
        internal var columns = 1
        internal var header: String? = null

        @DrawableRes
        internal var icon: Int = R.drawable.ic_error_toast_vector
        internal var urlicon: String? = null

        /**
         * Inflate the given menu resource to the options
         */
        fun add(@MenuRes menuRes: Int): Builder {
            options.add(OptionHolder(menuRes, null))
            return this
        }

        /**
         * Add an option to the sheet
         */
        fun add(option: OptionRequest): Builder {
            options.add(OptionHolder(null, option))
            return this
        }

        fun exclude(option: Int): Builder {
            excludes.add(option)
            return this
        }

        /**
         * Set the number of columns you want for your options
         */
        fun columns(columns: Int): Builder {
            this.columns = columns
            return this
        }

        /**
         * Add a custom header to the modal, using the custom layout if provided
         */
        fun header(header: String, @DrawableRes icon: Int, url: String?): Builder {
            this.header = header
            this.icon = icon
            this.urlicon = url
            return this
        }

        /**
         * Build the [ModalBottomSheetDialogFragment]. You still need to call [ModalBottomSheetDialogFragment.show] when you want it to show
         */
        fun build(listener: Listener): ModalBottomSheetDialogFragment {
            return newInstance(listener, this)
        }

        /**
         * Build and show the [ModalBottomSheetDialogFragment]
         */
        fun show(fragmentManager: FragmentManager, tag: String, listener: Listener): ModalBottomSheetDialogFragment {
            val dialog = build(listener)
            dialog.show(fragmentManager, tag)
            return dialog
        }
    }

    /**
     * Listener for when the modal options are selected
     */
    interface Listener {
        /**
         * A modal option has been selected
         */
        fun onModalOptionSelected(option: Option)
    }

    internal class Adapter(private val callback: (option: Option) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            const val VIEW_TYPE_HEADER = 0
            const val VIEW_TYPE_ITEM = 1
        }

        private val options = mutableListOf<Option>()
        private var layoutRes = R.layout.modal_bottom_sheet_dialog_fragment_item
        private var headerLayoutRes = R.layout.modal_bottom_sheet_dialog_fragment_header
        internal var header: String? = null

        @DrawableRes
        internal var icon: Int = R.drawable.ic_error_toast_vector
        internal var urlicon: String? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            when (viewType) {

                VIEW_TYPE_HEADER -> {
                    val view = LayoutInflater.from(parent.context).inflate(headerLayoutRes, parent, false)
                    return HeaderViewHolder(view)
                }
                VIEW_TYPE_ITEM -> {
                    val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
                    val holder = ItemViewHolder(view)
                    view.setOnClickListener {
                        val position = if (header != null) {
                            holder.bindingAdapterPosition - 1
                        } else {
                            holder.bindingAdapterPosition
                        }
                        val option = options[position]
                        callback.invoke(option)
                    }
                    return holder
                }
            }

            throw IllegalStateException("Wht is this")
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val correctedPosition = if (header == null) position else position - 1
            if (holder is ItemViewHolder) {
                val option = options[correctedPosition]
                holder.bind(option)
            } else if (holder is HeaderViewHolder) {
                holder.bind(header, urlicon, icon)
            }
        }

        override fun getItemCount(): Int {
            return if (header == null) options.size else options.size + 1
        }

        override fun getItemViewType(position: Int): Int {
            if (header != null) {
                if (position == 0) {
                    return VIEW_TYPE_HEADER
                }
            }
            return VIEW_TYPE_ITEM
        }

        fun set(options: List<Option>) {
            this.options.clear()
            this.options.addAll(options)
            notifyDataSetChanged()
        }
    }

    internal class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var text: TextView = view.findViewById(android.R.id.text1)
        private var icon: ImageView = view.findViewById(android.R.id.icon)

        fun bind(option: Option) {
            text.text = option.title
            icon.setImageDrawable(option.icon)
        }
    }

    internal class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var text: TextView = view.findViewById(android.R.id.text1)
        private var av: ImageView = view.findViewById(R.id.item_avatar)

        fun bind(header: String?, url: String?, @DrawableRes res: Int) {
            text.text = header
            PicassoInstance.with().cancelRequest(av)
            av.setImageResource(res)
            if (!Utils.isEmpty(url)) {
                PicassoInstance.with()
                        .load(url)
                        .transform(PolyTransformation())
                        .into(av)
            } else {
                Utils.setColorFilter(av, CurrentTheme.getColorPrimary(av.context))
            }
        }
    }

    init {
        this.listener = listener
    }

}
