package commonist.ui

import java.io.File
import java.util.{ Enumeration => JUEnumeration }
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

import scutil.base.implicits._
import scutil.core.implicits._
import scutil.lang.ISeq

/** a TreeNode for a File in the DirectoryTree */
final class FileNode(val file:File) extends DefaultMutableTreeNode {
	private var allowsChildrenValue	= false

	def childNodes:ISeq[FileNode]	=
			children()
			.asInstanceOf[JUEnumeration[FileNode]]
			.toIterator
			.toVector

	// NOTE without asInstanceOf scala chooses the Object constructor over the Object[] constructor
	def treePathClone:TreePath	=
			new TreePath(getPath.asInstanceOf[Array[Object]])

	/** ensures the node has a single child every directory below it */
	def update() {
		removeAllChildren()

		val listed	= file childrenWhere { file:File => file.isDirectory && !file.isHidden }
		allowsChildrenValue	= listed.isDefined
		listed foreach { files =>
			files sortBy { _.getPath } map { new FileNode(_) } foreach add
		}
	}

	//------------------------------------------------------------------------------

	override def getAllowsChildren():Boolean	= allowsChildrenValue
	override def isLeaf():Boolean				= false
	override def toString():String				= file.getName.optionNonEmpty getOrElse file.getPath
}
