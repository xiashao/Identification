package com.tfboss.login.util;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public enum TranObjectType
{
	CreateCommand,
	CommandHasBeenUsed,
	CommandCreatedSuccessful,
	CommandCreatedFail,

	Register,
	RegisterFail,
	RegisterSuccess,

	GetCommand,
	GetCommandFail,
	GetCommandSuccessful,

	AddCommandCheck,
	AddCommand,
	AddCommandNotFound,
	AddCommandHasFill,
	AddCommandFail,

	AddPassWay,
	DeletePassWay,
	UpdatePassWay,
	AlterPassWayFail,

	AddPassFail,
	AddSoundPass,
	AddFacePass,

	DeletePassFail,
	DeleteSoundPass,
	DeleteFacePass,

	LoginIn,
	LoginOut,
	LoginOutS,
	EndUse,

	FriendHelpRequest,
	FriendHelpRequest2,
	FriendHelpResponse,
	FriendHelpResponseRefuse,
	FriendHelpRequestWait,
	FriendHelpRequestFail,
	FriendHelpSuccess,
	FriendHelpFail,
}
