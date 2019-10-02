package `in`.evilcorp.bricksopenlauncher.presentation.shortcuts_view

import `in`.evilcorp.bricksopenlauncher.repository.entities.Shortcut

sealed class ChangeEvent
class SimpleClick(val shortcut: Shortcut): ChangeEvent()
class Reorder(val shortcut: Shortcut): ChangeEvent()